package com.cabutchei;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.cabutchei.notification.EventBus;
import com.cabutchei.notification.events.ServerState;



public class Listener implements NotificationListener{
    private EventBus bus;

    public Listener(EventBus bus) {
        this.bus = bus;
    }

    public void handleNotification(Notification notification, Object handback) {
        String notifType = notification.getType();
        System.out.println("[JMX] Received notification: " + notifType);
        var serverState = com.cabutchei.servers.ServerState.getByState(notifType);
        bus.publish(new ServerState(serverState));
    }
    
}
