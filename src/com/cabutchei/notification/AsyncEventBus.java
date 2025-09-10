package com.cabutchei.notification;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AsyncEventBus extends SimpleEventBus {

    protected final Executor executor;
    
    public AsyncEventBus(Executor executor) {
        this.executor = executor;
    }

    public static AsyncEventBus singleThreaded() {
        return new AsyncEventBus(Executors.newSingleThreadExecutor());
    }

    @Override
    public void publish(Object event) {
        executor.execute(() -> super.publish(event));
    }

}
