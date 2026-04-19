package com.example.serverautostarter.hetzner.enums;

import lombok.Getter;

import java.util.Arrays;

import static com.example.serverautostarter.common.service.SshService.DEFAULT_TIMEOUT_SECONDS;

@Getter
public enum ServerCommands {
    ROOT_PASS_CHANGING(ServerStatusEnum.READY_FOR_INITIAL_SCRIPTS, null , DEFAULT_TIMEOUT_SECONDS, 0),
    USER_CREATION(ServerStatusEnum.USER_CREATED, "sudo useradd -m amnezia" , DEFAULT_TIMEOUT_SECONDS, 1),
    PASS_CREATION(ServerStatusEnum.USER_PASS_CREATED, "echo \"amnezia:${AMNEZIA_PASS}\" | sudo chpasswd", DEFAULT_TIMEOUT_SECONDS, 2),
    SUDO_ENABLING(ServerStatusEnum.USER_ADDED_TO_SUDO, "sudo usermod -aG sudo amnezia", DEFAULT_TIMEOUT_SECONDS, 3),
    NO_PASS_ENABLED(ServerStatusEnum.USER_NO_PASS_ENABLED, "echo 'amnezia ALL=(ALL) NOPASSWD:ALL' | sudo tee /etc/sudoers.d/amnezia", DEFAULT_TIMEOUT_SECONDS, 4);
    ServerStatusEnum serverDesiredStatus;
    String script;
    Integer timeout;
    Integer order;
    ServerCommands(ServerStatusEnum serverDesiredStatus, String script, Integer timeout, Integer order) {
        this.serverDesiredStatus = serverDesiredStatus;
        this.script = script;
        this.timeout = timeout;
        this.order = order;
    }

    public static ServerCommands getCommandForNextStep(ServerStatusEnum currentServerStatusEnum) {
        if (currentServerStatusEnum.isInitial()) {
            return ROOT_PASS_CHANGING;
        }
        if (currentServerStatusEnum.isTerminal()) {
            return null;
        }
        return Arrays.stream(ServerCommands.values()).filter(command -> command.getServerDesiredStatus().equals(currentServerStatusEnum)).findFirst()
                .map(ServerCommands::getOrder)
                .map(order -> getCommandsByOrder(order + 1))
                .orElse(null);
    }

    private static ServerCommands getCommandsByOrder(Integer order) {
        return Arrays.stream(ServerCommands.values()).filter(command -> command.getOrder().equals(order)).findFirst().orElse(null);
    }
}
