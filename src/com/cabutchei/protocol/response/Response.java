package com.cabutchei.protocol.response;



public abstract class Response {
    String type = "response";
    Boolean success;
    String opcode;
    int version;
    String id;
    long timestamp;
}