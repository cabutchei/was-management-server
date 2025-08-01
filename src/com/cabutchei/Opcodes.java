package com.cabutchei;



public enum Opcodes {
    HANDSHAKE_REQUEST("Handshake.Request"),
    SERVER_STATUS("Server.Status"),
    SERVER_START("Server.Start"),
    SERVER_STOP("Server.Stop"),
    APPLICATION_STATUS("Application.Status"),
    APPLICATION_START("Application.Start"),
    APPLICATION_STOP("Application.Stop"),
    UNKNOWN("Unknown");

    private final String code;

    Opcodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Enum getByCode(String code) {
        for (Opcodes opcode : Opcodes.values()) {
            if (opcode.getCode().equals(code)) {
                return opcode;
            }
        }
        return UNKNOWN;
    }
}