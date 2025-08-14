package com.cabutchei;



import java.util.HashMap;
import java.util.Map;

/**
 * The ServerProcess class manages the lifecycle of server processes.
 * It allows for starting, stopping, and monitoring server processes.
 */

public class ServerProcess {

    private Map<String, Process> processes = new HashMap<>();


    public void startServer(String serverName) {
        String command = "C:/Desenvolvimento/IBM/WebSphere/AppServer_8_5/bin/startServer.bat ";
        ProcessBuilder processBuilder = new ProcessBuilder(command, serverName);
        try {
            processBuilder.start();
            System.out.println("Server " + serverName + " started.");
        } catch (Exception e) {
            System.err.println("Failed to start server " + serverName + ": " + e.getMessage());
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

    public void stopServer(String serverName) {
        String command = "C:/Desenvolvimento/IBM/WebSphere/AppServer_8_5/bin/stopServer.bat ";
        ProcessBuilder processBuilder = new ProcessBuilder(command, serverName);
        try {
            processBuilder.start();
            System.out.println("Server " + serverName + " stopped.");
        } catch (Exception e) {
            System.err.println("Failed to stop server " + serverName + ": " + e.getMessage());
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
