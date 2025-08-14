package com.cabutchei.protocol;

import com.cabutchei.Opcodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddServerResponse extends Response {

    // public Payload payload;

    public AddServerResponse(String id, String opcode, int version, long timestamp, Boolean success) {
        this.id = id;
        this.opcode = opcode;
        this.version = version;
        this.timestamp = timestamp;
        this.success = success;
        // this.payload = payload;
    }

    public AddServerResponse(String id, Opcodes opcode, int version, long timestamp, Boolean success) {
        this(id, opcode.getCode(), version, timestamp, success);
    }

    // public void setPayload(String id, String name, String version, String[] servers, String[] profiles) {
    //     this.payload = new Payload(id, name, version, servers, profiles);
    // }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this) + "\n";
    }

    // public static record Payload(
    //     String id,
    //     String name,
    //     String version,
    //     String[] servers,
    //     String[] profiles
    // ) {}
    
}
