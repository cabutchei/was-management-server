package com.cabutchei.protocol;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StartServerRequest extends Request {

    public Payload payload;

    public StartServerRequest(String id, Opcodes opcode, String version, long timestamp, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public StartServerRequest(String id, String opcode, String version, long timestamp, Payload payload) {
        this(id, Opcodes.getByCode(opcode), version, timestamp, payload);
    }

    public static StartServerRequest fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, StartServerRequest.class);
    }

    public static record Payload (
        String serverId
    ) {}

}