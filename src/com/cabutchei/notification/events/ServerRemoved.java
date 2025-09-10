package com.cabutchei.notification.events;


public class ServerRemoved extends Event {

    public String serverId;

    public ServerRemoved(String serverId) {
        this.serverId = serverId;
    }

}

