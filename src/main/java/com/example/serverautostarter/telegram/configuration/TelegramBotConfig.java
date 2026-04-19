package com.example.serverautostarter.telegram.configuration;

import com.example.serverautostarter.telegram.service.TelegramConnector;
import com.example.serverautostarter.utils.service.LogService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramBotConfig {

    LogService logService;
    TelegramConnector telegramConnector;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramConnector);
            logService.saveInfo("Успешная регистрация телеграм-бота");
        } catch (TelegramApiException e) {
            logService.saveError("Ошибка регистрации телеграм-бота:", e);
        }
    }
}