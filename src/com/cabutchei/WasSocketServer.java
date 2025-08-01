package com.cabutchei;



import java.net.ServerSocket;
import java.net.Socket;

public class WasSocketServer {
    public static void main(String[] args) throws Exception {

        WasFacade wasFacade = new WasFacade("localhost", "8880", "MyCell", "MyNode", "MyServer");
        
        ServerSocket serverSocket = new ServerSocket(9999);
        System.out.println("Java server listening on port 9999...");

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("[agent] Client connected: " + client.getRemoteSocketAddress());
            
            ClientSession session = new ClientSession(client);
            session.start();


            WasListener listener = new WasListener(session);
            wasFacade.subscribeToNotifications(listener);
        }
    }
}