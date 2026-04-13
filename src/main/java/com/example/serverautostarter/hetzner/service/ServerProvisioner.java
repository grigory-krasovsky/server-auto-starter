package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.common.dto.CommandRequestDto;

public interface ServerProvisioner {
    void runInitialScripts(String ip, String rootPass);
    void runSingleScript(String ip, String rootPass, CommandRequestDto command);
    void runTestScripts(String ip, String rootPass);

    boolean connectSuccessful(String ip, String rootPass);
}
