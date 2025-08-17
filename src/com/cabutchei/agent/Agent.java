package com.cabutchei.agent;



import java.util.Map;

import com.cabutchei.Opcodes;
import com.cabutchei.ServerProcess;
import com.cabutchei.commands.*;
import com.cabutchei.protocol.request.AddServerRequest;
import com.cabutchei.protocol.request.GetServerInfoRequest;
import com.cabutchei.protocol.request.StopServerRequest;
import com.cabutchei.protocol.response.AddServerResponse;
import com.cabutchei.protocol.response.GetServerInfoResponse;
import com.cabutchei.protocol.response.StartServerResponse;
import com.cabutchei.protocol.response.StopServerResponse;



public class Agent {

    Commands commandService;
    ServerProcess serverProcess;

    public Agent(Commands commandService, ServerProcess serverProcess) {
        this.commandService = commandService;
        this.serverProcess = serverProcess;
    }

    public String startServer(String line) {
        var req = AddServerRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_ADD) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        String serverId = req.payload.id();
        serverProcess.startServer(serverId);
        // String pid = commandService.startServer(serverId);
        var payload = new StartServerResponse.Payload(serverId);
        var resp = new StartServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), true, payload);
        return resp.toJson();
        // return "";
    }


    public String stopServer(String line) {
        var req = StopServerRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_STOP) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        String serverId = req.payload.serverId();
        serverProcess.stopServer(serverId);
        // commandService.stopServer(serverId);
        var payload = new StopServerResponse.Payload(serverId);
        var resp = new StopServerResponse(req.id, req.opcode, Integer.parseInt(req.version), System.currentTimeMillis(), true, payload);
        return resp.toJson();
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
