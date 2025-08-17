package com.cabutchei.protocol;



public abstract class Response {
    String type = "response";
    Boolean success;
    String opcode;
    int version;
    String id;
    long timestamp;
}