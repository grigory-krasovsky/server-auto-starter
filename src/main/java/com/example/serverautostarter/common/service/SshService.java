package com.example.serverautostarter.common.service;

import com.example.serverautostarter.common.dto.CommandRequest;
import com.example.serverautostarter.common.dto.CommandResult;

import java.util.List;
import java.util.Map;

public interface SshService {
    Map<CommandRequest, CommandResult> runInitialScripts(String ip, String rootPass, List<CommandRequest> commands);

    public static int DEFAULT_TIMEOUT_SECONDS = 10;
}
