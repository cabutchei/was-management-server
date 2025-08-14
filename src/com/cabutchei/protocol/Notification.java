package com.cabutchei.protocol;



import java.util.Map;


public class Notification {
    String type = "notification";
    String opcode;
    int version;
    long timestamp;
    Map<String, Object> payload;
}
