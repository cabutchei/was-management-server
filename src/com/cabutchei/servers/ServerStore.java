package com.cabutchei.servers;



import java.util.HashMap;
import java.util.Map;



public class ServerStore {

    private final Map<String, Server> registry = new HashMap<>();

    public void addServer(Server server) {
        this.registry.put(server.id(), server);
    }
    
    public Server getServer(String id) {
        return this.registry.get(id);
    }

    public Map<String, Server> getAllServers() {
        return new HashMap<>(this.registry);
    }

    public void removeServer(String id) {
        this.registry.remove(id);
    }
}
