package com.cabutchei;



import java.io.IOException;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cabutchei.notification.EventBus;
import com.cabutchei.notification.events.ProcessExit;
import com.cabutchei.process.ServerProcess;
import com.cabutchei.process.ProcessEventBridge;
import com.cabutchei.process.ServerProcessManager;

public class ServerRuntimeManager implements AutoCloseable {

    private final EventBus bus;
    private final WasFacadeService wasFacadeService;
    private final ServerProcessManager serverProcessManager;

    private final ConcurrentHashMap<String, ServerRuntimeManager.ServerContext> contexts = new ConcurrentHashMap<>();

    public ServerRuntimeManager(EventBus bus, WasFacadeService wasFacadeService, ServerProcessManager serverProcessManager) {
        this.bus = bus;
        this.wasFacadeService = wasFacadeService;
        this.serverProcessManager = serverProcessManager;
    }

    public void attach(String serverId) throws Exception {
        AtomicBoolean excp = new AtomicBoolean();
        excp.set(false);

        contexts.compute(serverId, (String id, ServerRuntimeManager.ServerContext prev) -> {
            closeQuietly(prev);
            ServerProcess serverProcess;
            try {
                serverProcess = serverProcessManager
                    .getProcess(serverId)
                    .orElse(serverProcessManager.start(serverId));

                ProcessEventBridge processBridge = new ProcessEventBridge(id, serverProcess, bus);
                JmxEventBridge jmxBridge = new JmxEventBridge(id, wasFacadeService, bus);

                return new ServerContext(serverProcess, processBridge, jmxBridge);
            } catch (IOException e) {
                excp.set(true);
                return null;
            }
        });

        if (excp.get()) {
            throw new Exception();
        }
    }

    public void detach(String serverId) {
        ServerContext context = contexts.remove(serverId);
        closeQuietly(context);
    }

    public void detach(String serverId, long delay) {
        ServerContext context = contexts.remove(serverId);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() { closeQuietly(context); }
        }, delay);
    }

    public void stopAndDetach(String serverId) {
        bus.once(ProcessExit.class, (e) -> detach(serverId));
        try { this.wasFacadeService.getFacade(serverId).stopServer(serverId); }
        catch (Exception e) { e.printStackTrace(); }
        this.wasFacadeService.invalidate(serverId);
    }

    public Optional<ServerContext> getContext(String serverId) {
        return Optional.ofNullable(contexts.get(serverId));
    }

    @Override
    public void close() {
        this.contexts.values().forEach(ServerRuntimeManager::closeQuietly);
        this.contexts.clear();
    }

    public static final class ServerContext implements AutoCloseable {
        private final ServerProcess serverProcess;
        private final ProcessEventBridge processBridge;
        private final JmxEventBridge jmxBridge;

        ServerContext(ServerProcess serverProcess, ProcessEventBridge processBridge, JmxEventBridge jmxBridge) {
            this.serverProcess = serverProcess;
            this.processBridge = processBridge;
            this.jmxBridge = jmxBridge;
        }

        public ServerProcess process() { return serverProcess; }

        @Override
        public void close() {
            // closeQuietly(jmxBridge);
            closeQuietly(processBridge);
        }
    }

    private static void closeQuietly(AutoCloseable c) {
        if (c != null) {
            try { c.close(); } catch (Exception ignored) {}
        }
    }
}
