package com.cabutchei;



import java.util.concurrent.*;

import com.cabutchei.notification.events.ServerRemoved;
import com.cabutchei.servers.Server;
import com.cabutchei.servers.ServerStore;

public final class WasFacadeService implements AutoCloseable {
    private final ServerStore store;

    // facadeRegistry keyed by serverId
    private final ConcurrentHashMap<String, WasFacade> facadeRegistry = new ConcurrentHashMap<>();

    // Optional: per-id lock to avoid parallel cold-starts
    private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    // Policy toggles
    private final boolean eagerConnectOnGet;
    private final int connectRetryCount;
    private final long connectBackoffMillis;

    public WasFacadeService(ServerStore store) {
        this(store, /*eager*/ false, /*retries*/ 20, /*backOff*/ 300L);
    }

    public WasFacadeService(ServerStore store,
                            boolean eagerConnectOnGet,
                            int connectRetryCount,
                            long connectBackoffMillis) {
        this.store = store;
        this.eagerConnectOnGet = eagerConnectOnGet;
        this.connectRetryCount = Math.max(0, connectRetryCount);
        this.connectBackoffMillis = Math.max(0, connectBackoffMillis);

        // Invalidate on server removal
        this.store.onServerRemoved(event -> invalidate(((ServerRemoved) event).serverId));
    }

    public WasFacade getFacade(String serverId) {
        // Fast path: healthy facadeRegistry’d facade
        WasFacade existing = facadeRegistry.get(serverId);
        if (existing != null && existing.isHealthy()) {
            if (eagerConnectOnGet) ensureConnectWithRetry(existing);
            return existing;
        }

        // Slow path: single-flight create/replace under per-id lock
        Object lock = locks.computeIfAbsent(serverId, k -> new Object());
        synchronized (lock) {
            WasFacade again = facadeRegistry.get(serverId);
            if (again != null && again.isHealthy()) {
                if (eagerConnectOnGet) ensureConnectWithRetry(again);
                return again;
            }

            closeQuietly(again); // close stale if present

            Server server = store.getServer(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown serverId: " + serverId));

            WasFacade fresh = new WasFacade("localhost", 8882,
                    "DF5088NB067Node02Cell", "DF5088NB067Node02", "server1");

            if (eagerConnectOnGet) ensureConnectWithRetry(fresh);

            facadeRegistry.put(serverId, fresh);
            return fresh;
        }
    }

    public void invalidate(String serverId) {
        WasFacade f = facadeRegistry.remove(serverId);
        closeQuietly(f);
        // optional: remove lock object to avoid leak of many serverIds
        locks.remove(serverId);
    }

    public void invalidateAll() {
        facadeRegistry.forEach((id, f) -> closeQuietly(f));
        facadeRegistry.clear();
        locks.clear();
    }

    @Override
    public void close() {
        invalidateAll();
    }

    // --- helpers ---
    private void ensureConnectWithRetry(WasFacade f) {
        int attempts = 0;
        while (true) {
            try {
                f.ensureConnected();
                return;
            } catch (RuntimeException e) {
                if (attempts++ >= connectRetryCount) throw e;
                sleep(connectBackoffMillis);
            }
        }
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static void closeQuietly(AutoCloseable c) {
        if (c != null) try { c.close(); } catch (Exception ignored) {}
    }
}
