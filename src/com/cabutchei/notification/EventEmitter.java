package com.cabutchei.notification;


import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventEmitter<T> implements AutoCloseable {
    private final CopyOnWriteArrayList<Consumer<? super T>> listeners = new CopyOnWriteArrayList<>();
    private volatile boolean closed = false;

    /** Subscribe; returns AutoCloseable to unsubscribe. */
    public Subscription on(Consumer<? super T> listener) {
        if (closed) throw new IllegalStateException("Emitter is closed");
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public Subscription once(Consumer<? super T> listener) {
        final Subscription[] ref = new Subscription[1];
        ref[0] = this.on(e -> {
            try { listener.accept(e); }
            finally {
                try { ref[0].close(); } catch (Exception ignored) {}
            }
        });
        return ref[0];
    }

    /** Emit to current listeners; never let one listener break others. */
    public void emit(T event) {
        for (var l : listeners) {
            try { l.accept(event); } catch (Throwable t) { /* log */ }
        }
    }

    @Override
    public void close() {
        closed = true;
        listeners.clear();
    }
}
