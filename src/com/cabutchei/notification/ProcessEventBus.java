package com.cabutchei.notification;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ProcessEventBus extends AsyncEventBus {
    
    public ProcessEventBus(Executor executor) {
        super(executor);
    }

    public static ProcessEventBus singleThreaded() {
        return new ProcessEventBus(Executors.newSingleThreadExecutor());
    }
}
