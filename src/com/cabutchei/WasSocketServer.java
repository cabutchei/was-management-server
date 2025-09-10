package com.cabutchei;



import java.net.ServerSocket;
import java.net.Socket;

import com.cabutchei.agent.Agent;
import com.cabutchei.commands.CommandService;
import com.cabutchei.notification.EventBus;
import com.cabutchei.notification.AsyncEventBus;
import com.cabutchei.notification.ClientNotificationService;
import com.cabutchei.process.ServerProcessManager;
import com.cabutchei.servers.ServerStore;

public class WasSocketServer {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Java server listening on port 9999...");

        var serverStore = new ServerStore();
        var wasFacadeService = new WasFacadeService(serverStore, true, 30, 500);
        var serverProcessManager = new ServerProcessManager();
        
        
        try {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("[agent] Client connected: " + client.getRemoteSocketAddress());
                
                EventBus bus = AsyncEventBus.singleThreaded();
                var serverRuntimeManager = new ServerRuntimeManager(bus, wasFacadeService, serverProcessManager);
                var commandService = new CommandService(serverStore, wasFacadeService, serverRuntimeManager);
                var agent = new Agent(commandService);
                
                ClientSession session = new ClientSession(client, agent);
                new ClientNotificationService(session, bus);
                session.start();
    
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