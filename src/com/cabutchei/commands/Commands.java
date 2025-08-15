package com.cabutchei.commands;


import java.util.List;
import java.util.Map;

import com.cabutchei.protocol.Protocol;
import com.cabutchei.protocol.Response;
import com.cabutchei.servers.Server;
import com.cabutchei.servers.ServerStore;
import com.ibm.websphere.product.WASProductInfo;
import com.cabutchei.Opcodes;
import com.cabutchei.WasConfig;



public class Commands {

    ServerStore serverStore;

    public Commands(ServerStore serverStore){
        this.serverStore = serverStore;
    }
    
    public String startServer(String serverId) {
        // Implementation for starting a server
        String pid = "12345"; // Placeholder for actual process ID
        System.out.println("Server " + serverId + " started.");
        return pid;
    }
    
    public String addServer(String serverId, String installDir) {
        var server = new Server(serverId, "name", installDir);
        serverStore.addServer(server);
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
