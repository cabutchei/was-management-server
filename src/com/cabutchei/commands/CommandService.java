package com.cabutchei.commands;



import java.util.List;

import com.cabutchei.servers.Server;
import com.cabutchei.servers.ServerStore;
import com.cabutchei.WasConfig;
import com.cabutchei.WasFacadeService;
import com.cabutchei.ServerRuntimeManager;



public class CommandService {

    ServerStore serverStore;
    ServerRuntimeManager serverRuntimeManager;
    WasFacadeService wasFacadeService;

    public CommandService(ServerStore serverStore, WasFacadeService wasFacadeService, ServerRuntimeManager serverRuntimeManager) {
        this.serverStore = serverStore;
        this.serverRuntimeManager = serverRuntimeManager;
        this.wasFacadeService = wasFacadeService;
    }

    public void startServer(String serverId) throws Exception {
        this.serverRuntimeManager.attach(serverId);
        this.wasFacadeService.getFacade(serverId);
    }

    public void stopServer(String serverId) {
        this.serverRuntimeManager.stopAndDetach(serverId);
    }

    public String addServer(String serverId, String installDir) {
    var server = new Server(serverId, "name", installDir);
    try {
        serverStore.addServer(server);
    } catch (Exception e) {
        // TODO auto-generated catch block
        e.printStackTrace();
    }

    System.out.println("Server " + serverId + " added with path: " + installDir);
    return serverId;
}

    public ServerInfo getServerInfo(String path) throws Exception {
        var wasConfig = new WasConfig(path);
        var productInfo = wasConfig.getServerInfo();
        var serverInfo = new ServerInfo(
            productInfo.getId(),
            productInfo.getName(),
            List.of("server1"), // TODO: need to actually parse the server names from install path
            wasConfig.getProfileNames(),
            wasConfig.getServerType()
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

    public record ServerInfo(String productId, String name, List<String> servers, List<String> profiles, String serverType) {}

}
