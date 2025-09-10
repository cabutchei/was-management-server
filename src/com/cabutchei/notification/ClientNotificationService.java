package com.cabutchei.notification;


import java.util.function.Consumer;

import com.cabutchei.ClientSession;
import com.cabutchei.notification.events.Event;
import com.cabutchei.notification.events.ProcessExit;
import com.cabutchei.notification.events.ProcessStart;
import com.cabutchei.notification.events.ServerState;

public class ClientNotificationService {

    private final ClientSession session;
    private final EventBus bus;

    public ClientNotificationService(ClientSession session, EventBus bus) {
        this.session = session;
        this.bus = bus;
        onProcessStart();
        onServerStarting();
        onServerStarted();
        onServerStopping();
        onProcessExit();
    }

    private Subscription onServerStarting() {
        String message = String.format(
            "{\"type\":\"event\",\"opcode\":\"%s\",\"version\":1,\"timestamp\":%d,\"payload\":{}}",
            "j2ee.state.starting", System.currentTimeMillis(), ""
        );
        return this.bus.subscribe(ServerState.class,
            (event) -> {
                if (event.serverState == com.cabutchei.servers.ServerState.STARTING) {
                    push(message);
                }
            });
    }

    private Subscription onServerStarted() {
        String message = String.format(
            "{\"type\":\"event\",\"opcode\":\"%s\",\"version\":1,\"timestamp\":%d,\"payload\":{\"message\":\"%s\"}}",
            "j2ee.state.running", System.currentTimeMillis(), ""
        );
        return this.bus.subscribe(ServerState.class,
            (event) -> {
                System.out.println("x: started");
                if (event.serverState == com.cabutchei.servers.ServerState.STARTED) {
                    System.out.println("x: pushing started event");
                    push(message);
                }
            });
    }

    private Subscription onServerStopping() {
        String message = String.format(
            "{\"type\":\"event\",\"opcode\":\"%s\",\"version\":1,\"timestamp\":%d,\"payload\":{\"message\":\"%s\"}}",
            "j2ee.state.stopping", System.currentTimeMillis(), ""
        );
        return this.bus.subscribe(ServerState.class,
            (event) -> {
                if (event.serverState == com.cabutchei.servers.ServerState.STOPPING) {
                    push(message);
                }
            });
    }

    private Subscription onProcessStart() {
        String message = String.format(
            "{\"type\":\"event\",\"opcode\":\"%s\",\"version\":1,\"timestamp\":%d,\"payload\":{\"message\":\"%s\"}}",
            "j2ee.state.starting", System.currentTimeMillis(), ""
        );
        return this.bus.subscribe(ProcessStart.class, (event) -> push(message));
    }

    private Subscription onProcessExit() {
        String message = String.format(
            "{\"type\":\"event\",\"opcode\":\"%s\",\"version\":1,\"timestamp\":%d,\"payload\":{\"message\":\"%s\"}}",
            "j2ee.state.stopped", System.currentTimeMillis(), "");
        return this.bus.subscribe(
            ProcessStart.class,
            (event) -> push(message)
            );
    }

    private void push(String json) {
        try {
            this.session.pushMessage(json);
        } catch (InterruptedException e) {
            System.out.println("Client closed");
        }
    }

}