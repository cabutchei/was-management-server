package com.cabutchei.process;


import java.io.IOException;
import java.util.function.Consumer;

import com.cabutchei.notification.EventEmitter;
import com.cabutchei.notification.Subscription;
import com.cabutchei.notification.events.Event;
import com.cabutchei.notification.events.ProcessExit;
import com.cabutchei.notification.events.ProcessStart;
import com.cabutchei.notification.events.ProcessStartFailure;

public class ServerProcess {

    private ProcessBuilder builder;
    private Process process;
    private EventEmitter<Event> emitter = new EventEmitter<>();
    private String scriptPath;

    public ServerProcess(String scriptPath) {
        this.scriptPath = scriptPath;
        this.builder = create();
    }

    private ProcessBuilder create() {
        var builder = new ProcessBuilder(this.scriptPath); // TODO: write command
        builder.inheritIO();
        return builder;
    }

    public Process start() throws IOException {
        Process p = this.builder.start();
        System.out.println("Parent process pid: " + p.pid());
        this.emitter.emit(new ProcessStart());
        var exit = p.onExit();
        exit.thenAccept((proc) -> {
            System.out.println("x: Exit");
            this.emitter.emit(new ProcessExit());
        });
        return p;
    }

    public void stop() {
        this.process.destroy();
    }

    public Process terminate() {
        return this.process.destroyForcibly();
    }

    public Subscription onStart(Consumer<Event> callback) {
        return this.emitter.on(event -> {
            if (event instanceof ProcessStart) {
                callback.accept(event);
            }
        });
    }

    public Subscription onFailure(Consumer<Event> callback) {
        return this.emitter.on(event -> {
            if (event instanceof ProcessStartFailure) {
                callback.accept(event);
            }
        });
    }

    public Subscription onExit(Consumer<Event> callback) {
        return this.emitter.on(event -> {
            if (event instanceof ProcessExit) {
                callback.accept(event);
            }
        });
    }

}
