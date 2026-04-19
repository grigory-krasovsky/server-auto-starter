package com.example.serverautostarter.telegram.service;

import com.example.serverautostarter.utils.service.LogService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramConnector extends TelegramLongPollingBot {

    @Value("${TELEGRAM_TOKEN}")
    private String telegramToken;

    @Value("${TELEGRAM_USERNAME}")
    private String telegramBotUsername;

    private final LogService logService;

    private final TelegramService telegramService;


    @PostConstruct
    public void init() {
        logService.saveInfo("Инициализация TelegramConnector. Имя бота:: " + telegramBotUsername);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                TelegramService.CallbackResult result = telegramService.handleCallbackQuery(update);
                if (result != null) {
                    if (result.editMessage()) {
                        editMessage(result.chatId(), result.messageId(), result.text(), result.keyboard());
                    } else {
                        sendMessage(result.chatId(), result.text(), result.keyboard());
                    }
                }
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();
                String username = update.getMessage().getFrom().getUserName();

                logService.saveInfo(String.format("Команда от @%s: %s", username, messageText));

                TelegramService.MessageResult result = telegramService.handleTextMessage(chatId, messageText);
                if (result != null) {
                    sendMessage(result.chatId(), result.text(), result.keyboard());
                }
            }
        } catch (Exception e) {
            logService.saveError("Ошибка обработки update", e);
            if (update.hasMessage()) {
                sendMessage(update.getMessage().getChatId(), "❌ Произошла ошибка. Попробуйте позже.", null);
            }
        }
    }

    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup replyMarkup) {
        try {
            SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .parseMode("MARKDOWN");

            if (replyMarkup != null) {
                messageBuilder.replyMarkup(replyMarkup);
            }

            execute(messageBuilder.build());
            logService.saveInfo(String.format("Сообщение отправлено в чат %d", chatId));
        } catch (TelegramApiException e) {
            logService.saveError(String.format("Ошибка отправки сообщения в чат %d", chatId), e);
        }
    }

    public void editMessage(Long chatId, Integer messageId, String newText, InlineKeyboardMarkup replyMarkup) {
        try {
            var editMessage = org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(newText)
                    .parseMode("MARKDOWN");

            if (replyMarkup != null) {
                editMessage.replyMarkup(replyMarkup);
            }

            execute(editMessage.build());
            logService.saveInfo(String.format("Сообщение отредактировано в чате %d", chatId));
        } catch (TelegramApiException e) {
            logService.saveError(String.format("Ошибка редактирования сообщения в чате %d", chatId), e);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotUsername;
    }

    @Override
    public String getBotToken() {
        return telegramToken;
    }
}
