package com.example.serverautostarter.hetzner.enums;

import lombok.Getter;

public enum ServerStatusEnum {
    WAITING_FOR_ROOT_PASS_CHANGE ("Сервер создан и готов смене дефолтного пароля", true, false),
    READY_FOR_INITIAL_SCRIPTS ("Сервер готов к прокатке необходимых скриптов", false, false),
    USER_CREATED ("Пользователь для ВПН клиента создан", false, false),
    USER_PASS_CREATED ("Пароль для пользователя создан", false, false),
    USER_ADDED_TO_SUDO ("Пользователь добавлен в группу sudo", false, false),
    USER_NO_PASS_ENABLED ("Пользователю дано разрешение выполнять команды без запроса пароля", false, false),
    READY_FOR_AMNEZIA ("Сервер готов для установки ВПН", false, true);

    @Getter
    private String description;
    @Getter
    private boolean initial;
    @Getter
    private boolean terminal;

    ServerStatusEnum(String description, boolean initial, boolean terminal) {
        this.description = description;
        this.initial = initial;
        this.terminal = terminal;
    }
}
