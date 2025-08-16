package com.cabutchei;



import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.cabutchei.agent.Agent;
import com.cabutchei.protocol.Protocol;


/**
 * The ClientSession class handles communication with a connected client.
 * It processes incoming requests and sends responses or notifications.
 */
public class ClientSession {
    private Socket client;
    private BlockingQueue<String> outbox;
    private Agent agent;

    public ClientSession(Socket client, Agent agent) {
        this.client = client;
        this.outbox = new LinkedBlockingQueue<>();
        this.agent = agent;
    }

    public void start() {
        // Start request handling in one thread.
        new Thread(() -> handleRequests()).start();
        // Start the notification sending in another thread.
        new Thread(() -> sendNotifications()).start();
    }

    private void handleRequests() {
        try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {

            client.setTcpNoDelay(true);
            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank())
                    continue;

                System.out.println("[agent] RX: " + line);

                String opcode = extract(line, "opcode");
                String id = extract(line, "id");
                String middle = null;
                String response = null;
                Map<String, Object> payload;
                switch (Opcodes.getByCode(opcode)) {
                    case Opcodes.HANDSHAKE_REQUEST:
                        payload = Map.of(
                            "selected", 1
                        );
                        response = Protocol.createResponse(true, opcode, id, payload);
                        break;
                    case Opcodes.SERVER_STATUS:
                        payload = Map.of(
                            "state", "STOPPED"
                        );
                        response = Protocol.createResponse(true, opcode, id, payload);
                        break;
                    case Opcodes.SERVER_INFO:
                        response = agent.getServerInfo(line);
                        break;
                    case Opcodes.SERVER_ADD:
                        response = agent.addServer(line);
                        break;
                    case Opcodes.SERVER_START:
                        response = agent.startServer(line);
                        break;
                    case Opcodes.SERVER_STOP:
                    response = agent.stopServer(line);
                        break;
                    default:
                        middle = "\"success\":false,\"error\":{\"code\":\"UNKNOWN_OPCODE\",\"message\":\"Unsupported opcode: " + opcode + "\"}";
                        break;
                }
                if (middle != null) {
                    response = jsonResponse(opcode, id, middle);
                }
                System.out.println("[agent] TX: " + response);
                out.write(response);
                out.flush();
            }
           
            System.out.println("[agent] Client closed (EOF).");
        } catch (Exception e) {
            System.err.println("[agent] Exception in client request handling: " + e.getMessage());
        } finally {
            close();
        }
    }

    private void sendNotifications() {
       
        try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {
            while (true) {
                String msg = outbox.take();
                out.write(msg + "\n");
                out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("[agent] Notification thread closed: " + e.getMessage());
        }
    }

    public void pushMessage(String message) throws InterruptedException {
        outbox.put(message);
    }

    private String jsonResponse(String opcode, String id, String middle) {
        return "{\"type\":\"response\",\"opcode\":\"" + opcode + "\",\"version\":1," +
               "\"id\":\"" + id + "\",\"timestamp\":" + System.currentTimeMillis() + "," + middle + "}\n";
    }

    private String extract(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0)
            return "";
        int colon = json.indexOf(':', k);
        int firstQ = json.indexOf('"', colon + 1);
        int secondQ = json.indexOf('"', firstQ + 1);
        return (firstQ >= 0 && secondQ > firstQ) ? json.substring(firstQ + 1, secondQ) : "";
    }

    public void close() {
        try {
            client.close();
        } catch (Exception ignore) { }
        System.out.println("[agent] Client session closed.");
    }
}
