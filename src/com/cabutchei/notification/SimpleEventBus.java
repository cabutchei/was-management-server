package com.cabutchei.notification;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class SimpleEventBus implements EventBus {

    private final Map<Class<?>, CopyOnWriteArrayList<Consumer<?>>> handlers = new ConcurrentHashMap<>();

    @Override
    public <T> Subscription subscribe(Class<T> type, Consumer<? super T> handler) {
        var list = handlers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
        Consumer<?> casted = (Consumer<?>) handler;
        list.add(casted);
        return () -> list.remove(casted);
    }

    public <T> Subscription once(Class<T> type, Consumer<? super T> handler) {
    final AtomicBoolean done = new AtomicBoolean(false);
    final AtomicReference<AutoCloseable> subRef = new AtomicReference<>();

    Consumer<T> wrapper = e -> {
        if (done.compareAndSet(false, true)) {
            try {
                handler.accept(e);
            } finally {
                Subscription s = (Subscription) subRef.get();
                if (s != null) {
                    try { s.close(); } catch (Exception ignore) {}
                }
            }
        }
    };

    Subscription sub = subscribe(type, wrapper);
    subRef.set(sub);

    return new Subscription() {
        private final AtomicBoolean closed = new AtomicBoolean(false);

        @Override
        public void close() {
            if (closed.compareAndSet(false, true)) {
                done.set(true);
                sub.close();
            }
        }
    };
}

    public void publish(Object event) {
        if (event == null) return;

        Class<?> et = event.getClass();
        deliverTo(handlers.get(et), event);

        for (var e : handlers.entrySet()) {
            Class<?> key = e.getKey();
            if (key != et && key.isAssignableFrom(et)) {
                deliverTo(e.getValue(), event);
            }
        }
    }

    public static void deliverTo(List<Consumer<?>> list, Object event) {
        if (list == null) return;
        for (Consumer<?> consumer : list) {
            try {
                @SuppressWarnings("unchecked")
                Consumer<Object> consumerObject = (Consumer<Object>) consumer;
                consumerObject.accept(event);
            } catch (Throwable t) {
                System.err.println("Event handler error: " + t);
            }
        }
    }
}

