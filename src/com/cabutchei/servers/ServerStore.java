package com.cabutchei.servers;



import java.util.HashMap;
import java.util.Map;

import com.cabutchei.WasFacade;



public class ServerStore {

    private final Map<String, Server> registry = new HashMap<>();
    private final Map<String, WasFacade> facadeRegistry = new HashMap<>();

    public void addServer(Server server) throws Exception {
        this.registry.put(server.id(), server);
        this.facadeRegistry.put(server.id(), new WasFacade("localhost", 8882, "DF5088NB067Node02Cell", "DF5088NB067Node02", "server1"));
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

    public WasFacade getFacade(String serverId) {
        return this.facadeRegistry.get(serverId);
    }
}
