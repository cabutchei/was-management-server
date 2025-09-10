package com.cabutchei.process;


import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import java.io.IOException;
import java.nio.file.Path;


public class ServerProcessManager {

    private final ConcurrentHashMap<String, ServerProcess> processes = new ConcurrentHashMap<>();

    public ServerProcess start(String serverId) throws IOException {
        // var scriptPath = new File("script/startServer.bat");

        Path p = Path.of("").toAbsolutePath();
        Path scriptPath = p.resolve("script/startServer.bat");
        ServerProcess serverProcess = new ServerProcess(scriptPath.toString());
        processes.put(serverId, serverProcess);
        serverProcess.start();
        return serverProcess;
    }

    public void stop(String serverId) {
        ServerProcess p = this.processes.get(serverId);
        p.stop();
        this.processes.remove(serverId);
    }

    public Optional<ServerProcess> getProcess(String serverId) {
        return Optional.ofNullable(this.processes.get(serverId));
    }
}
