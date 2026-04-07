package com.example.serverautostarter.hetzner.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public enum ServerStatus {
    READY_FOR_INITIAL_SCRIPTS ("Сервер создан и готов к прокатке необходимых скриптов"),
    USER_CREATED ("Пользователь для ВПН клиента создан"),
    USER_PASS_CREATED ("Пароль для пользователя создан"),
    USER_ADDED_TO_SUDO ("Пользователь добавлен в группу sudo"),
    USER_NO_PASS_ENABLED ("Пользователю дано разрешение выполнять команды без запроса пароля");

    @Getter
    private String description;

    ServerStatus(String description) {
        this.description = description;
    }
}
