package com.cabutchei.notification;

import java.util.function.Consumer;


public interface EventBus {
    <T> Subscription subscribe(Class<T> eventType, Consumer<? super T> handler);
    <T> Subscription once(Class<T> eventType, Consumer<? super T> handler);
    void publish(Object event);
}
