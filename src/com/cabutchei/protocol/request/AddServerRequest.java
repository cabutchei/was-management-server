package com.cabutchei.protocol.request;



import org.python.modules.time;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class AddServerRequest extends Request {

    public Payload payload;

    public AddServerRequest(String id, Opcodes opcode, String version, long timestamp, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public AddServerRequest(String id, String opcode, String version, long timestamp, Payload payload) {
        this(id, Opcodes.getByCode(opcode), version, timestamp, payload);
    }

    public static AddServerRequest fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, AddServerRequest.class);
    }
    
    public static record Payload(
        String id,
        String name,
        String path,
        String server,
        String profile
        ) {}
}

