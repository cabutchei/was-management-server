package com.cabutchei.protocol.response;



public abstract class Response {
    String type = "response";
    Boolean success;
    String opcode;
    int version;
    String id;
    long timestamp;
    Error error;

    public static record Error(int code, String message){}

    public void setError(int code, String message) {
        if (this.success) throw new RuntimeException("Cannot set 'error' if 'success' is true");

        this.error = new Error(code, message);
        
    }
}