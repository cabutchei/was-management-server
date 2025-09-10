package com.cabutchei.protocol.request;



import com.cabutchei.Opcodes;


public abstract class Request {
    public String type = "request";
    public String id;
    public Opcodes opcode;
    public String version;
    public long timestamp;


    public String extract(String json, String key) {
        int k = json.indexOf("\"" + key + "\"");
        if (k < 0)
            return "";
        int colon = json.indexOf(':', k);
        int firstQ = json.indexOf('"', colon + 1);
        int secondQ = json.indexOf('"', firstQ + 1);
        return (firstQ >= 0 && secondQ > firstQ) ? json.substring(firstQ + 1, secondQ) : "";
    }

}
