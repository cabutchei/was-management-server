package com.cabutchei;



import java.util.HashMap;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The ServerProcess class manages the lifecycle of server processes.
 * It allows for starting, stopping, and monitoring server processes.
 */

public class ServerProcess {

    private Map<String, Process> processes = new HashMap<>();


    public void startServer(String serverId) {
        // String command = "C:/Desenvolvimento/IBM/WebSphere/AppServer_8_5/bin/startServer.bat";
        Path scriptPath = Paths.get(".").getParent().getParent().getParent()
            .resolve("script").resolve("startServer.bat").toAbsolutePath();
        // ProcessBuilder processBuilder = new ProcessBuilder(command, "-profileName", "AppSrv03", "-script", scriptPath.toString(), "server1");
        // try {
        //     processBuilder.start();
        //     System.out.println("Server " + serverId + " started.");
        // } catch (Exception e) {
        //     System.err.println("Failed to start server " + serverId + ": " + e.getMessage());
        // }
        ProcessBuilder serverProcessBuilder = new ProcessBuilder(scriptPath.toString());
        try{
            serverProcessBuilder.start();
            processes.put(serverId, serverProcessBuilder.start());
            System.out.println("Server " + serverId + " started.");
        } catch (Exception e) {
            System.err.println("Failed to start server " + serverId + ": " + e.getMessage());
        }
        // ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", serverName + ".jar");
        // try {
        //     Process process = processBuilder.start();
        //     processes.put(serverName, process);
        //     System.out.println("Server " + serverName + " started.");
        // } catch (Exception e) {
        //     System.err.println("Failed to start server " + serverName + ": " + e.getMessage());
        // }
    }

    public void stopServer(String serverId) {
        String command = "C:/Desenvolvimento/IBM/WebSphere/AppServer_8_5/bin/stopServer.bat";
        ProcessBuilder processBuilder = new ProcessBuilder(command, "server1", "-profileName", "AppSrv03");
        try {
            processBuilder.start();
            System.out.println("Server " + serverId + " stopped.");
        } catch (Exception e) {
            System.err.println("Failed to stop server " + serverId + ": " + e.getMessage());
        }
        // Process process = processes.get(serverName);
        // if (process != null) {
        //     process.destroy();
        //     processes.remove(serverName);
        //     System.out.println("Server " + serverName + " stopped.");
        // } else {
        //     System.err.println("No running server found with name: " + serverName);
        // }
    }

}
