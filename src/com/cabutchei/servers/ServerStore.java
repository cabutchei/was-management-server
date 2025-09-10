package com.cabutchei.servers;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.cabutchei.WasFacade;
import com.cabutchei.notification.events.Event;
import com.cabutchei.notification.EventEmitter;
import com.cabutchei.notification.Subscription;
import com.cabutchei.notification.events.ServerRemoved;



public class ServerStore {

    private final Map<String, Server> registry = new HashMap<>();
    private final Map<String, WasFacade> facadeRegistry = new HashMap<>();
    private final EventEmitter<Event> emitter = new EventEmitter<>();

    public void addServer(Server server) throws Exception {
        this.registry.put(server.id(), server);
        this.facadeRegistry.put(server.id(), new WasFacade("localhost", 8882, 
            "DF5088NB067Node02Cell", "DF5088NB067Node02Node", "server1"));
        // TODO: get jmx soap port from wasconfig
        // TODO: servers should have cell and node info
    }

    public Optional<Server> getServer(String id) {
        return Optional.ofNullable(this.registry.get(id));
    }

    public Map<String, Server> getAllServers() {
        return new HashMap<>(this.registry);
    }

    public void removeServer(String id) {
        this.registry.remove(id);
        this.emitter.emit(new ServerRemoved(id));
    }

    public WasFacade getFacade(String serverId) {
        return this.facadeRegistry.get(serverId);
    }

    public Subscription onServerRemoved(Consumer<Event> callback) {
        return this.emitter.on(event -> {
            if (event instanceof ServerRemoved) {
                callback.accept(event);
            }
        });
    }
}
