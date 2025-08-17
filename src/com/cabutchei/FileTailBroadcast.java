package com.cabutchei;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class FileTailBroadcast {
    private volatile Tailer tailer;
    private LogChannel logChannel;

    public FileTailBroadcast() {
        this.logChannel = logChannel;
        this.tailer = tailer;
    }

    // public static void subscribe(Session session) {
    //     subscribers.add(session);
    //     ensureStarted();
    // }

    // public static void unsubscribe(Session session) {
    //     subscribers.remove(session);
    // }

    public void close() {
        this.tailer.close();
        this.tailer = null;
    }

    public synchronized void watch() {
        if (tailer != null) return;
        File file = new File("/Desenvolvimento/IBM/Websphere/AppServer_8_5/profiles/AppSrv03/logs/server1/SystemOut.log");
        Charset charset = Charset.forName("UTF-8");
        TailerListenerAdapter listener = new TailerListenerAdapter() {
            @Override public void handle(String line) {
                try {
                    logChannel.pushMessage(line);
                } catch (Exception e) {}
            }
            @Override public void fileRotated() { /* optional: notify */ }
            @Override public void fileNotFound() { /* optional: backoff/notify */ }
            @Override public void handle(Exception ex) { ex.printStackTrace(); }
        };

        tailer = Tailer.create(file, charset, listener, 250, true, true, 4096);
    }
}
