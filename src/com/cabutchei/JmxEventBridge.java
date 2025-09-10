package com.cabutchei;


import javax.management.NotificationListener;

import com.cabutchei.notification.EventBus;

public class JmxEventBridge {

    public JmxEventBridge(String serverId, WasFacadeService wasFacadeService, EventBus bus) {
        WasFacade wasFacade = wasFacadeService.getFacade(serverId);
        NotificationListener listener = new Listener(bus);
        try {
            wasFacade.addServerlistener(listener);
        } catch (Exception e) {
            System.out.println("Could not attach listener");
            e.printStackTrace();
        }
    }
    
}
