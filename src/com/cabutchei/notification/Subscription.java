package com.cabutchei.notification;

public interface Subscription extends AutoCloseable {
    @Override void close();
}
