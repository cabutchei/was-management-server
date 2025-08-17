package com.cabutchei.protocol.response;

import java.util.List;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StartServerResponse extends Response {

    public Payload payload;

    public StartServerResponse(String id, String opcode, int version, long timestamp, Boolean success, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.success = success;
        this.payload = payload;
    }

    public StartServerResponse(String id, Opcodes opcode, int version, long timestamp, Boolean success, Payload payload) {
        this(id, opcode.getCode(), version, timestamp, success, payload);
    }

    public void setPayload(String id) {
        this.payload = new Payload(id);
    }

    public static StartServerResponse fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, StartServerResponse.class);
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this) + "\n";
    }

    public static record Payload (
        String serverId
    ) {}
}
