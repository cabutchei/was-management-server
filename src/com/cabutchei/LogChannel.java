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
 * The LogChannel class handles communication with a connected client.
 * It processes incoming requests and sends responses or notifications.
 */
public class LogChannel {
    private Socket client;
    private BlockingQueue<String> outbox;
    private FileTailBroadcast fileTailBroadcast;
    private Boolean subscribed = false;


    public LogChannel(Socket client, Agent agent) {
        this.client = client;
        this.outbox = new LinkedBlockingQueue<>();
        this.fileTailBroadcast = new FileTailBroadcast();
    }

    public void start() {
        new Thread(() -> handleRequests()).start();
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
                    case Opcodes.SUBSCRIBE:
                        if (subscribed) {
                            break;
                        }
                        response = Protocol.createResponse(true, opcode, id, null);
                        System.out.println("[agent] TX: " + response);
                        out.write(response);
                        out.flush();
                        this.fileTailBroadcast.watch();
                        new Thread(() -> streamLogs()).start();
                        break;
                    default:
                        middle = "\"success\":false,\"error\":{\"code\":\"UNKNOWN_OPCODE\",\"message\":\"Unsupported opcode: " + opcode + "\"}";
                        break;
                }
                if (middle != null) {
                    response = jsonResponse(opcode, id, middle);
                }

            }
           
            System.out.println("[agent] Client closed (EOF).");
        } catch (Exception e) {
            System.err.println("[agent] Exception in client request handling: " + e.getMessage());
        } finally {
            this.fileTailBroadcast.close();
            close();
        }
    }


    private void streamLogs() {
        try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8))) {
            while (true) {
                String msg = outbox.take();
                out.write(msg + "\n");
                out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("[agent] Log streaming thread closed: " + e.getMessage());
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
