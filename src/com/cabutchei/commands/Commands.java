package com.cabutchei.commands;


import java.util.List;
import java.util.Map;

import com.cabutchei.protocol.Protocol;
import com.cabutchei.protocol.Response;
import com.ibm.websphere.product.WASProductInfo;
import com.cabutchei.Opcodes;
import com.cabutchei.WasConfig;



public class Commands {
    
    public static String startServer(String serverId) {
        // Implementation for starting a server
        String pid = "12345"; // Placeholder for actual process ID
        System.out.println("Server " + serverId + " started.");
        return pid;
    }
    
    public static String addServer(String serverId, String installDir) {
        // Implementation for adding a server
        // TODO: Implement server registry
        return "";
    }

    public static ServerInfo getServerInfo(String path) {
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

    public static String getVersion(String path) {
        var wasConfig = new WasConfig(path);
        return wasConfig.getVersion();
    }

    public static String getName(String path) {
        var wasConfig = new WasConfig(path);
        return wasConfig.getName();
    }

    public static List<String> getProfiles(String path) {
        var wasConfig = new WasConfig(path);
        return wasConfig.getProfiles().stream().map(profile -> profile.getName()).toList();
    }

    public static record ServerInfo(String productId, String version, String name, List<String> servers, List<String> profiles) {}
    

}
