package com.cabutchei.protocol;



import java.util.Map;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The Protocol class encapsulates the message formats used by the server.
 * It provides helper methods to create standardized JSON responses and notifications.
 */
public class Protocol {
    private static final Gson gson = new GsonBuilder().create();

    /**
     * Creates a JSON response message.
     *
     * @param opcode  the operation code that identifies the request type.
     * @param id      the unique identifier of the request.
     * @param content the content of the response, e.g., payload or error information.
     * @return the JSON string representing the response.
     */
    public static String createResponse(Boolean success, String opcode, String id, Map<String, Object> payload) {
        Response response = new Response();
        response.success = success;
        response.opcode = opcode;
        response.version = 1;
        response.id = id;
        response.timestamp = System.currentTimeMillis();
        response.payload = payload;
        return gson.toJson(response) + "\n";
    }

    public static String createResponse(Boolean success, Opcodes opcode, String id, Map<String, Object> payload) {
        String opcodeStr = opcode.getCode();
        return createResponse(success, opcodeStr, id, payload);
    }

    /**
     * Creates a JSON notification message.
     *
     * @param opcode  the operation code that identifies the notification.
     * @param content the content of the notification.
     * @return the JSON string representing the notification.
     */
    public static String createNotification(String opcode, Map<String, Object> content) {
        Notification notification = new Notification();
        notification.opcode = opcode;
        notification.version = 1;
        notification.timestamp = System.currentTimeMillis();
        notification.payload = content;
        return gson.toJson(notification) + "\n";
    }

    // // Inner classes representing the response and notification structures.

    public static class Response {
        String type = "response";
        Boolean success;
        String opcode;
        int version;
        String id;
        long timestamp;
        Map<String, Object> payload;
    }

    public static class Notification {
        String type = "notification";
        String opcode;
        int version;
        long timestamp;
        Map<String, Object> payload;
    }
}