package com.cabutchei.agent;



import java.util.Map;

import com.cabutchei.Opcodes;
import com.cabutchei.commands.*;
import com.cabutchei.protocol.AddServerRequest;
import com.cabutchei.protocol.GetServerInfoRequest;
import com.cabutchei.protocol.GetServerInfoResponse;
import com.cabutchei.protocol.AddServerResponse;



public class Agent {

    Commands commandService;

    public Agent(Commands commandService) {
        this.commandService = commandService;
    }

    public String startServer(String line) {
        var req = AddServerRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_ADD) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        String serverId = req.payload.id();
        String pid = commandService.startServer(serverId);
        // var resp = new StartServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), true, pid);
        // return resp.toJson();
        return "";
    }
    
    public String addServer(String line) {
        var req = AddServerRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_ADD) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        String installDir = req.payload.path();
        String serverId = req.payload.id();
        commandService.addServer(serverId, installDir);
        var resp = new AddServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), true);
        return resp.toJson();
    }

    public String getServerInfo(String line) {
        GetServerInfoRequest req = GetServerInfoRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_INFO) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        var serverInfo = commandService.getServerInfo(req.payload.path());
        var resp = new GetServerInfoResponse(req.id, req.opcode, 0, 0, null, null);
        resp.setPayload(serverInfo.productId(),
                        serverInfo.name(),
                        serverInfo.version(),
                        serverInfo.servers(),
                        serverInfo.profiles());

        return resp.toJson();
    }
    
}
