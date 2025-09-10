package com.cabutchei;

import com.google.gson.annotations.SerializedName;

public enum Opcodes {

    @SerializedName("Echo") ECHO("Echo"),
    @SerializedName("Handshake.Request") HANDSHAKE_REQUEST("Handshake.Request"),
    @SerializedName("Server.Info") SERVER_INFO("Server.Info"),
    @SerializedName("Server.Add") SERVER_ADD("Server.Add"),
    @SerializedName("Server.Status") SERVER_STATUS("Server.Status"),
    @SerializedName("Server.Start") SERVER_START("Server.Start"),
    @SerializedName("Server.Stop") SERVER_STOP("Server.Stop"),
    @SerializedName("Application.Status") APPLICATION_STATUS("Application.Status"),
    @SerializedName("Application.Start") APPLICATION_START("Application.Start"),
    @SerializedName("Application.Stop") APPLICATION_STOP("Application.Stop"),
    @SerializedName("Subscribe") SUBSCRIBE("Subscribe"),
    UNKNOWN("Unknown");

    private final String code;

    Opcodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Opcodes getByCode(String code) {
        for (Opcodes opcode : Opcodes.values()) {
            if (opcode.getCode().equals(code)) {
                return opcode;
            }
        }
        return UNKNOWN;
    }
}