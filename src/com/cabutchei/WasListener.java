package com.cabutchei;



import javax.management.Notification;
import javax.management.NotificationListener;

public class WasListener implements NotificationListener {
    private final ClientSession clientSession;
    

    public WasListener(ClientSession clientSession) {
        this.clientSession = clientSession;
    }
    
    @Override
    public void handleNotification(Notification notification, Object handback) {
        String notificationMessage = String.format(
                "{\"type\":\"notification\",\"opcode\":\"%s\",\"version\":1,\"timestamp\":%d,\"content\":{\"message\":\"%s\"}}",
                notification.getType(), System.currentTimeMillis(), notification.getMessage());
        System.out.println("Received notification: " + notification.getType());
        System.out.println("Message: " + notification.getMessage());
        try {
            clientSession.pushMessage(notificationMessage);
        } catch (InterruptedException e) {
            System.err.println("Failed to push notification: " + e.getMessage());
        }
    }
}