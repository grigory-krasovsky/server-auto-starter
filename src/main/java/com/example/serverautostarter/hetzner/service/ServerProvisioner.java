package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.enums.ServerStatus;

public interface ServerProvisioner {
    void runInitialScripts(String ip, String rootPass);
    void runScripts(String ip, String rootPass, ServerStatus initialStatus);
    void runTestScripts(String ip, String rootPass);
}
