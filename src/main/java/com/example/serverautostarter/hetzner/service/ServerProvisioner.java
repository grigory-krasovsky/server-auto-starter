package com.example.serverautostarter.hetzner.service;

public interface ServerProvisioner {
    void runInitialScripts(String ip, String rootPass);
    void runTestScripts(String ip, String rootPass);
}
