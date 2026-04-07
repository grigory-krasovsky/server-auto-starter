package com.example.serverautostarter.hetzner.enums;

import lombok.Getter;

import static com.example.serverautostarter.common.service.SshService.DEFAULT_TIMEOUT_SECONDS;

@Getter
public enum ServerCommands {
    USER_CREATION(ServerStatus.USER_CREATED, "sudo useradd -m amnezia" , DEFAULT_TIMEOUT_SECONDS, 1),
    //Todo amnezia pass is dangerous here. should be taken from env variables
    PASS_CREATION(ServerStatus.USER_PASS_CREATED, "sudo passwd ${AMNEZIA_PASS}", DEFAULT_TIMEOUT_SECONDS, 2),
    SUDO_ENABLING(ServerStatus.USER_ADDED_TO_SUDO, "sudo usermod -aG sudo amnezia", DEFAULT_TIMEOUT_SECONDS, 3),
    NO_PASS_ENABLED(ServerStatus.USER_NO_PASS_ENABLED, "echo 'amnezia ALL=(ALL) NOPASSWD:ALL' | sudo tee /etc/sudoers.d/amnezia", DEFAULT_TIMEOUT_SECONDS, 4);
    ServerStatus serverDesiredStatus;
    String script;
    Integer timeout;
    Integer order;
    ServerCommands(ServerStatus serverDesiredStatus, String script, Integer timeout, Integer order) {
        this.serverDesiredStatus = serverDesiredStatus;
        this.script = script;
        this.timeout = timeout;
        this.order = order;
    }
}
