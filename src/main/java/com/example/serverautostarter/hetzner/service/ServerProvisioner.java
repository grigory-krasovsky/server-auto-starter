package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;

import java.util.Map;

public interface ServerProvisioner {
    void runInitialScripts(String ip, String rootPass);
    Map<CommandRequestDto, CommandResultDto> runSingleScript(String ip, String rootPass, CommandRequestDto command);
    void runTestScripts(String ip, String rootPass);

    boolean connectSuccessful(String ip, String rootPass);
}
