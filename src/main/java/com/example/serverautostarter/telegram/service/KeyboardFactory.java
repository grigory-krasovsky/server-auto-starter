package com.example.serverautostarter.telegram.service;

import com.example.serverautostarter.hetzner.db.entity.Server;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardFactory {
    public InlineKeyboardMarkup createServerListWithRemoveButtons(List<Server> servers) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Server server : servers) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            // Кнопка с названием сервера (просто информационная, без действия)
            InlineKeyboardButton serverButton = new InlineKeyboardButton();
            serverButton.setText(String.format("🖥️ %s (IP: %s)", server.getName(), server.getIp()));
            serverButton.setCallbackData("noop"); // ничего не делает
            row.add(serverButton);

            // Кнопка удаления
            InlineKeyboardButton deleteButton = new InlineKeyboardButton();
            deleteButton.setText("❌ Удалить");
            deleteButton.setCallbackData("delete_server_" + server.getId());
            row.add(deleteButton);

            keyboard.add(row);
        }

        // Кнопка обновления списка
        List<InlineKeyboardButton> refreshRow = new ArrayList<>();
        InlineKeyboardButton refreshButton = new InlineKeyboardButton();
        refreshButton.setText("🔄 Обновить список");
        refreshButton.setCallbackData("refresh_list");
        refreshRow.add(refreshButton);
        keyboard.add(refreshRow);

        markup.setKeyboard(keyboard);
        return markup;
    }

    // Клавиатура для подтверждения создания сервера
    public InlineKeyboardMarkup createConfirmCreationKeyboard(String serverName) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        InlineKeyboardButton confirmButton = new InlineKeyboardButton();
        confirmButton.setText("✅ Да, создать");
        confirmButton.setCallbackData("confirm_create_" + serverName);
        row.add(confirmButton);

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Отмена");
        cancelButton.setCallbackData("cancel_create");
        row.add(cancelButton);

        keyboard.add(row);
        markup.setKeyboard(keyboard);
        return markup;
    }

    public InlineKeyboardMarkup createDatacenterKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // Дата-центры Hetzner
        String[][] datacenters = {
                {"fsn1", "🇩🇪 Falkenstein (FSN1)"},
                {"hel1", "🇫🇮 Helsinki (HEL1)"},
                {"nbg1", "🇩🇪 Nuremberg (NBG1)"}
        };

        for (String[] dc : datacenters) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(dc[1]);
            button.setCallbackData("select_dc_" + dc[0]);
            row.add(button);
            keyboard.add(row);
        }

        // Кнопка отмены
        List<InlineKeyboardButton> cancelRow = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌ Отмена");
        cancelButton.setCallbackData("cancel_create");
        cancelRow.add(cancelButton);
        keyboard.add(cancelRow);

        markup.setKeyboard(keyboard);
        return markup;
    }
}
