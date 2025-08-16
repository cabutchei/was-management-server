package com.cabutchei.protocol;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StopServerResponse extends Response {

    public Payload payload;

    public StopServerResponse(String id, String opcode, int version, long timestamp, Boolean success, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.success = success;
        this.payload = payload;
    }

    public StopServerResponse(String id, Opcodes opcode, int version, long timestamp, Boolean success, Payload payload) {
        this(id, opcode.getCode(), version, timestamp, success, payload);
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this) + "\n";
    }

    public static record Payload (
        String serverId
    ) {}

}
