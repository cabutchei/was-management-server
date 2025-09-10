package com.cabutchei.protocol.request;



import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GetServerInfoRequest extends Request{

    public Payload payload;

    public GetServerInfoRequest(String id, Opcodes opcode, String version, long timestamp, Payload payload) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public GetServerInfoRequest(String id, String opcode, String version, long timestamp, Payload payload) {
        this(id, Opcodes.getByCode(opcode), version, timestamp, payload);
    }

    public static GetServerInfoRequest fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, GetServerInfoRequest.class);
    }
    
    public static record Payload (
        String path
    ) {}
    
}
