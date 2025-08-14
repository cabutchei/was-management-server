package com.cabutchei.protocol;

import java.util.List;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetServerInfoResponse extends Response {

    public Payload payload;

    public GetServerInfoResponse(String id, String opcode, int version, long timestamp, Boolean success, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.success = success;
        this.payload = payload;
    }

    public GetServerInfoResponse(String id, Opcodes opcode, int version, long timestamp, Boolean success, Payload payload) {
        this(id, opcode.getCode(), version, timestamp, success, payload);
    }

    public static GetServerInfoResponse fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, GetServerInfoResponse.class);
    }

    public void setPayload(String id, String name, String version, List<String> servers, List<String> profiles) {
        this.payload = new Payload(id, name, version, servers, profiles);
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this) + "\n";
    }

    public static record Payload(
        String id,
        String name,
        String version,
        List<String> servers,
        List<String> profiles
    ) {}
    
}
