package com.cabutchei.notification;


import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationFilter;
import java.util.function.Predicate;

public class NotificationFilterFactory {

    private NotificationFilterFactory() {}

    public static NotificationFilter of(Predicate<Notification> p) {
        class Filter implements NotificationFilter {
            @Override
            public boolean isNotificationEnabled(Notification notification) {
                return p.test(notification);
            }
        }
        return new Filter();
    }

    public static NotificationFilter attributeChange(Predicate<AttributeChangeNotification> p) {
        return notification -> (notification instanceof AttributeChangeNotification)
            && p.test((AttributeChangeNotification) notification);
    }

    public static void func(Notification notification) {
        NotificationFilter f = serverState();
        System.out.println(f.isNotificationEnabled(notification));
    }

    public static NotificationFilter serverState() {
        return of(notification -> notification.getType().contains("j2ee.state"));
    }

    public static NotificationFilter startingState() {
        return of(notification -> notification.getType().equals("j2ee.state.starting"));
    }

    public static NotificationFilter startedState() {
        return of(notification -> notification.getType().equals("j2ee.state.running"));
    }

    public static NotificationFilter stoppingState() {
        return of(notification -> notification.getType().equals("j2ee.state.stopping"));
    }

    public static NotificationFilter stoppedState() {
        return of(notification -> notification.getType().equals("j2ee.state.stopped"));
    }
}
