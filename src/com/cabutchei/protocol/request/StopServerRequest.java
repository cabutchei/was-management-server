package com.cabutchei.protocol.request;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StopServerRequest extends Request {

    public Payload payload;

    public StopServerRequest(String id, Opcodes opcode, String version, long timestamp, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public StopServerRequest(String id, String opcode, String version, long timestamp, Payload payload) {
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
