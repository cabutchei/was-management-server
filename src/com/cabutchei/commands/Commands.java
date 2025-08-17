package com.cabutchei.commands;


import java.util.List;

import com.cabutchei.servers.Server;
import com.cabutchei.servers.ServerStore;
import com.cabutchei.ClientSession;
import com.cabutchei.ServerProcess;
import com.cabutchei.WasConfig;
import com.cabutchei.WasListener;



public class Commands {

    ServerStore serverStore;
    ServerProcess serverProcess;

    public Commands(ServerStore serverStore, ServerProcess serverProcess){
        this.serverStore = serverStore;
        this.serverProcess = serverProcess;
    }
    
    public void startServer(String serverId, ClientSession session) {
        serverProcess.startServer(serverId);
        var listener = new WasListener(session);
        try {
            var wasFacade = serverStore.getFacade(serverId);
            wasFacade.connect(10000, 2000);
            wasFacade.subscribeToNotifications(listener);
        } catch (Exception e) {}
    }
    
    public String addServer(String serverId, String installDir) {
        var server = new Server(serverId, "name", installDir);
        try {
        serverStore.addServer(server);
        } catch (Exception e) {}
        System.out.println("Server " + serverId + " added with path: " + installDir);
        return serverId;
    }

    // provisional. Should this be here, or should the caller have a store instance?
    public Server getServer(String id) {
        return serverStore.getServer(id);
    }

    public ServerInfo getServerInfo(String path) {
        var wasConfig = new WasConfig(path);
        var productInfo = wasConfig.getServerInfo();
        var serverInfo = new ServerInfo(
            productInfo.getId(),
            productInfo.getVersion(),
            productInfo.getName(),
            List.of("server1"), // Placeholder for actual server names
            wasConfig.getProfileNames()
        );
        return serverInfo;
    }

    public String getVersion(String path) {
        var wasConfig = new WasConfig(path);
        return wasConfig.getVersion();
    }

    public String getName(String path) {
        var wasConfig = new WasConfig(path);
        return wasConfig.getName();
    }

    public List<String> getProfiles(String path) {
        var wasConfig = new WasConfig(path);
        return wasConfig.getProfiles().stream().map(profile -> profile.getName()).toList();
    }

    public record ServerInfo(String productId, String version, String name, List<String> servers, List<String> profiles) {}
    

}
