package com.example.serverautostarter.common.service;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;

import java.util.List;
import java.util.Map;

public interface SshService {
    Map<CommandRequestDto, CommandResultDto> runInitialScripts(String ip, String rootPass, List<CommandRequestDto> commands);

    public static int DEFAULT_TIMEOUT_SECONDS = 10;
}
