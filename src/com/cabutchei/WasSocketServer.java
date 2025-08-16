package com.cabutchei;



import java.net.ServerSocket;
import java.net.Socket;

import com.cabutchei.agent.Agent;
import com.cabutchei.commands.Commands;
import com.cabutchei.servers.ServerStore;

public class WasSocketServer {
    public static void main(String[] args) throws Exception {

        var serverStore = new ServerStore();
        var commandService = new Commands(serverStore);
        var serverProcess = new ServerProcess();
        var agent = new Agent(commandService, serverProcess);

        WasFacade wasFacade = new WasFacade("localhost", "8880", "MyCell", "MyNode", "MyServer");
        
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Java server listening on port 9999...");

        try{
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("[agent] Client connected: " + client.getRemoteSocketAddress());
                
                ClientSession session = new ClientSession(client, agent);
                session.start();
    
    
                WasListener listener = new WasListener(session);
                wasFacade.subscribeToNotifications(listener);
            }
        } catch (Exception e) {
            System.err.println("Error in server: " + e.getMessage());
        } finally {
            serverSocket.close();
            System.out.println("Server socket closed.");
        }


        // serverSocket.close(); // Uncomment if you want to close the server socket after use
    }
}