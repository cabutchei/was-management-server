package com.cabutchei.servers;

public enum ServerState {
    STARTING("j2ee.state.starting"),
    STARTED("j2ee.state.started"),
    STOPPING("j2ee.state.stopping"),
    STOPPED("j2ee.state.stopped");

    private String state;

    ServerState(String state) {
        this.state = state;
    }

    public static String getStateString(ServerState serverState) {
        return serverState.state;
    }
    
    public static ServerState getByState(String state) {
        for (ServerState ss : ServerState.values()) {
            if (ss.state.equals(state)) {
                return ss;
            }
        }
        return null;
    }
}
