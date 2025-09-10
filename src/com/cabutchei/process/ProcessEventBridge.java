package com.cabutchei.process;


import java.util.ArrayList;
import java.util.List;

import com.cabutchei.notification.EventBus;
import com.cabutchei.notification.Subscription;



public class ProcessEventBridge implements AutoCloseable {

    private final List<Subscription> subs = new ArrayList<>();

    public ProcessEventBridge(String serverId, ServerProcess serverProcess, EventBus bus) {
        subs.add(serverProcess.onFailure(e -> bus.publish(e)));
        subs.add(serverProcess.onStart(e -> bus.publish(e)));
        subs.add(serverProcess.onExit(e -> bus.publish(e)));
    }

    @Override
    public void close() {
        subs.forEach(s -> {
            try { s.close(); } catch (Exception ignored) {}
        });
    }
}
