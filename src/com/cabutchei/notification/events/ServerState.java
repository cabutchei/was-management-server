package com.cabutchei.notification.events;

public class ServerState extends Event {
    
    public com.cabutchei.servers.ServerState serverState;

    public ServerState(com.cabutchei.servers.ServerState serverState) {
        this.serverState = serverState;
    }

}
