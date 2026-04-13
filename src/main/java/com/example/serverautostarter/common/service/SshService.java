package com.example.serverautostarter.common.service;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;
import com.example.serverautostarter.utils.service.LogService;

import java.util.List;
import java.util.Map;

public interface SshService {
    Map<CommandRequestDto, CommandResultDto> runScripts(String ip, String rootPass, String amneziaPass, List<CommandRequestDto> commands, LogService logService);

    int DEFAULT_TIMEOUT_SECONDS = 10;
    boolean connectSuccessful(String ip, String rootPass);
}
