package com.cabutchei.agent;



import com.cabutchei.ClientSession;
import com.cabutchei.Opcodes;
import com.cabutchei.commands.CommandService;
import com.cabutchei.commands.CommandService.ServerInfo;
import com.cabutchei.protocol.request.AddServerRequest;
import com.cabutchei.protocol.request.GetServerInfoRequest;
import com.cabutchei.protocol.request.StartServerRequest;
import com.cabutchei.protocol.request.StopServerRequest;
import com.cabutchei.protocol.response.AddServerResponse;
import com.cabutchei.protocol.response.GetServerInfoResponse;
import com.cabutchei.protocol.response.StartServerResponse;
import com.cabutchei.protocol.response.StopServerResponse;



public class Agent {

    CommandService commandService;

    public Agent(CommandService commandService) {
        this.commandService = commandService;
    }

    public String startServer(String line, ClientSession session) {
        var req = StartServerRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_START) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        String serverId = req.payload.serverId();
        StartServerResponse.Payload payload;
        StartServerResponse resp;
        try {
            commandService.startServer(serverId);
            payload = new StartServerResponse.Payload(serverId);
            resp = new StartServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), true, payload);
        } catch(Exception e) {
            resp = new StartServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), false, null);
            resp.setError(0, e.getMessage());
        }
        return resp.toJson();
    }


    public String stopServer(String line) {
        var req = StopServerRequest.fromJson(line);
        if (req.opcode != Opcodes.SERVER_STOP) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        if (req.opcode != Opcodes.SERVER_STOP) {
            throw new IllegalArgumentException("Invalid opcode: " + req.opcode);
        }
        String serverId = req.payload.serverId();
        StopServerResponse.Payload payload;
        StopServerResponse resp;
        try {
            commandService.stopServer(serverId);
            payload = new StopServerResponse.Payload(serverId);
            resp = new StopServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), true, payload);
        } catch(Exception e) {
            resp = new StopServerResponse(req.id, req.opcode.getCode(), Integer.parseInt(req.version), System.currentTimeMillis(), false, null);
            resp.setError(0, e.getMessage());
        }
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
        ServerInfo serverInfo;
        try {
            serverInfo = commandService.getServerInfo(req.payload.path());
        } catch(Exception e) {
            var respError = new GetServerInfoResponse(req.id, req.opcode, 0, 0, null, null);
            respError.setError(0, e.getMessage());
            return respError.toJson();
        }

        GetServerInfoResponse resp = new GetServerInfoResponse(req.id, req.opcode, 0, 0, null, null);
        resp.setPayload(serverInfo.productId(),
                        serverInfo.name(),
                        serverInfo.servers(),
                        serverInfo.profiles(),
                        serverInfo.serverType());

        return resp.toJson();
    }
    
}
