package com.example.serverautostarter.telegram.service;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import com.example.serverautostarter.hetzner.enums.ServerStatusEnum;
import com.example.serverautostarter.hetzner.service.ServerManager;
import com.example.serverautostarter.hetzner.service.ServerService;
import com.example.serverautostarter.hetzner.service.ServerStatusService;
import com.example.serverautostarter.utils.service.LogService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramService {

    ServerService serverService;
    ServerStatusService serverStatusService;
    ServerManager serverManager;
    KeyboardFactory keyboardFactory;
    LogService logService;

    Map<Long, String> chatIdToServerName = new ConcurrentHashMap<>();

    private final Map<Long, ServerCreationContext> chatIdToCreationContext = new ConcurrentHashMap<>();

    @Value
    public static class ServerCreationContext {
        String serverName;
        String datacenter;
        CreationStep step;
    }

    public enum CreationStep {
        AWAITING_NAME,
        AWAITING_DATACENTER
    }

    public MessageResult handleTextMessage(Long chatId, String messageText) {
        // Если пользователь в процессе создания сервера (ожидает ввод имени)
        if (chatIdToCreationContext.containsKey(chatId)) {
            ServerCreationContext context = chatIdToCreationContext.get(chatId);

            if (context.getStep() == CreationStep.AWAITING_NAME) {
                return handleServerNameInput(chatId, messageText);
            }
        }

        // Обычные команды
        if (messageText.equals("/remove")) {
            return handleRemoveCommand(chatId);
        }

        if (messageText.equals("/add")) {
            return handleAddCommand(chatId);
        }

        if (messageText.equals("/cancel")) {
            return handleCancelCommand(chatId);
        }

        if (messageText.equals("/list")) {
            return handleListCommand(chatId);
        }

        return null;
    }

    public CallbackResult handleCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String username = update.getCallbackQuery().getFrom().getUserName();

        logService.saveInfo(String.format("Коллбек от @%s: %s", username, callbackData));

        if (callbackData.equals("refresh_list")) {
            return handleRefreshList(chatId, messageId);
        }

        if (callbackData.startsWith("select_dc_")) {
            return handleDatacenterSelection(chatId, messageId, callbackData);
        }

        if (callbackData.startsWith("delete_server_")) {
            return handleDeleteServer(chatId, messageId, callbackData, username);
        }
        if (callbackData.startsWith("confirm_create_")) {
            return handleConfirmCreate(chatId, messageId, callbackData, username);
        }

        if (callbackData.equals("cancel_create")) {
            return handleCancelCreate(chatId, messageId);
        }

        return null;
    }

    private CallbackResult handleDatacenterSelection(Long chatId, Integer messageId, String callbackData) {
        String datacenter = callbackData.replace("select_dc_", "");

        ServerCreationContext context = chatIdToCreationContext.get(chatId);
        if (context == null || context.getStep() != CreationStep.AWAITING_DATACENTER) {
            return new CallbackResult(chatId, messageId, "❌ Сессия создания сервера истекла. Начните заново с /add", null, true);
        }

        // Сохраняем дата-центр и показываем подтверждение
        ServerCreationContext updatedContext = new ServerCreationContext(
                context.getServerName(), datacenter, CreationStep.AWAITING_DATACENTER
        );
        chatIdToCreationContext.put(chatId, updatedContext);

        String confirmMessage = String.format(
                "📝 *Подтверждение создания сервера*\n\n" +
                        "🖥️ *Название:* %s\n" +
                        "🗺️ *Дата-центр:* %s\n\n" +
                        "Создать сервер?",
                context.getServerName(),
                getDatacenterName(datacenter)
        );

        InlineKeyboardMarkup keyboard = keyboardFactory.createConfirmCreationKeyboard(context.getServerName());
        return new CallbackResult(chatId, messageId, confirmMessage, keyboard, true);
    }

    private String getDatacenterName(String dc) {
        return switch (dc) {
            case "fsn1" -> "Франкфурт (Falkenstein) FSN1";
            case "hel1" -> "Хельсинки HEL1";
            case "nbg1" -> "Нюрнберг NBG1";
            default -> dc;
        };
    }

    private MessageResult handleAddCommand(Long chatId) {
        // Создаем контекст и переходим к шагу ввода имени
        chatIdToCreationContext.put(chatId, new ServerCreationContext(null, null, CreationStep.AWAITING_NAME));

        String message = "📝 *Создание нового сервера*\n\n" +
                "Введите название сервера:\n\n" +
                "*(для отмены введите /cancel)*";

        return new MessageResult(chatId, message, null);
    }


    private MessageResult handleServerNameInput(Long chatId, String serverName) {
        if (serverName.equals("/cancel")) {
            chatIdToCreationContext.remove(chatId);
            return new MessageResult(chatId, "❌ Создание сервера отменено.", null);
        }

        // Сохраняем имя и переходим к выбору дата-центра
        ServerCreationContext updatedContext = new ServerCreationContext(
                serverName, null, CreationStep.AWAITING_DATACENTER
        );
        chatIdToCreationContext.put(chatId, updatedContext);

        String message = "📝 *Выберите дата-центр для сервера:*\n\n" +
                "Где будет расположен сервер?";

        InlineKeyboardMarkup keyboard = keyboardFactory.createDatacenterKeyboard();
        return new MessageResult(chatId, message, keyboard);
    }

    private CallbackResult handleConfirmCreate(Long chatId, Integer messageId, String callbackData, String username) {
        String serverName = callbackData.replace("confirm_create_", "");

        // Получаем контекст с дата-центром
        ServerCreationContext context = chatIdToCreationContext.remove(chatId);

        if (context == null) {
            return new CallbackResult(chatId, messageId, "❌ Сессия создания сервера истекла. Начните заново с /add", null, true);
        }

        try {
            // Создаем сервер с указанием дата-центра
            ServerRequestDto requestDto = ServerRequestDto.builder()
                    .name(serverName)
                    .dataCenter(context.getDatacenter())
                    .build();

            serverManager.createNewServer(requestDto);
            Server createdServer = serverService.findByName(serverName);

            logService.saveInfo(String.format("Пользователь @%s создал сервер: %s (IP: %s, DC: %s)",
                    username, createdServer.getName(), createdServer.getIp(), context.getDatacenter()));

            String successMessage = String.format(
                    """
                    ✅ *СЕРВЕР СОЗДАН* ✅
                    
                    📌 *IP:* `%s`
                    🖥️ *Название:* %s
                    🗺️ *Дата-центр:* %s
                    
                    Используйте /list для просмотра всех серверов.""",
                    createdServer.getIp(),
                    createdServer.getName(),
                    getDatacenterName(context.getDatacenter())
            );

            return new CallbackResult(chatId, messageId, successMessage, null, true);

        } catch (Exception e) {
            logService.saveError("Ошибка создания сервера", e);
            return new CallbackResult(chatId, messageId,
                    "❌ Ошибка при создании сервера. Попробуйте позже.",
                    null, true);
        }
    }

    private CallbackResult handleCancelCreate(Long chatId, Integer messageId) {
        chatIdToCreationContext.remove(chatId);
        return new CallbackResult(chatId, messageId, "❌ Создание сервера отменено.", null, true);
    }

    private MessageResult handleCancelCommand(Long chatId) {
        if (chatIdToCreationContext.containsKey(chatId)) {
            chatIdToCreationContext.remove(chatId);
            return new MessageResult(chatId, "❌ Создание сервера отменено.", null);
        }
        return new MessageResult(chatId, "Нет активных операций для отмены.", null);
    }

    private MessageResult handleRemoveCommand(Long chatId) {
        List<Server> servers = serverService.findAll();

        if (servers.isEmpty()) {
            return new MessageResult(chatId, "📭 Нет доступных серверов для удаления.", null);
        }

        String message = """
                🗑️ *Список серверов:*

                Нажмите ❌ Удалить рядом с сервером, который хотите удалить.""";

        InlineKeyboardMarkup keyboard = keyboardFactory.createServerListWithRemoveButtons(servers);
        return new MessageResult(chatId, message, keyboard);
    }

    private MessageResult handleListCommand(Long chatId) {
        Map<Server, ServerStatus> serverToStatus = serverService.findAll()
                .stream().collect(Collectors.toMap(Function.identity(), serverStatusService::getServerCurrentStatus));


        if (serverToStatus.isEmpty()) {
            return new MessageResult(chatId, "📭 Нет доступных серверов.", null);
        }

        String table = "```\n" + serverToStatus.entrySet().stream()
                .map(entry -> String.format("%d | %s | %s | %s %s",
                        entry.getKey().getId(),
                        entry.getKey().getName(),
                        entry.getKey().getIp() != null ? entry.getKey().getIp() : "0.0.0.0",
                        getStatusEmoji(entry.getValue().getStatus()),
                        entry.getValue().getStatus())
                )
                .collect(Collectors.joining("\n")) + "\n```";

        String message = String.format("📋 *Список серверов:*\n\n%s", table);

        return new MessageResult(chatId, message, null);
    }

    private String getStatusEmoji(ServerStatusEnum status) {
        return switch (status) {
            case WAITING_FOR_ROOT_PASS_CHANGE -> "🔄";
            case READY_FOR_INITIAL_SCRIPTS -> "⚙️";
            case USER_CREATED -> "👤";
            case USER_PASS_CREATED -> "🔑";
            case USER_ADDED_TO_SUDO -> "🔓";
            case USER_NO_PASS_ENABLED -> "🚀";
            case READY_FOR_AMNEZIA -> "✅";
        };
    }

    private CallbackResult handleRefreshList(Long chatId, Integer messageId) {
        List<Server> servers = serverService.findAll();

        if (servers.isEmpty()) {
            return new CallbackResult(chatId, messageId, "📭 Нет доступных серверов для удаления.", null, true);
        }

        String message = """
                🗑️ *Список серверов:*

                Нажмите ❌ Удалить рядом с сервером, который хотите удалить.""";

        InlineKeyboardMarkup keyboard = keyboardFactory.createServerListWithRemoveButtons(servers);
        return new CallbackResult(chatId, messageId, message, keyboard, true);
    }

    private CallbackResult handleDeleteServer(Long chatId, Integer messageId, String callbackData, String username) {
        String serverIdStr = callbackData.replace("delete_server_", "");

        try {
            Long serverId = Long.parseLong(serverIdStr);
            Server server = serverService.findById(serverId);

            if (server == null) {
                logService.saveInfo(String.format("Сервер %d не найден", serverId));
                return new CallbackResult(chatId, messageId, "❌ Сервер не найден.", null, true);
            }

            serverManager.deleteServer(serverId);

            logService.saveInfo(String.format("Сервер %s (ID: %d) удален пользователем @%s",
                    server.getName(), serverId, username));

            // После удаления обновляем список
            List<Server> remainingServers = serverService.findAll();

            if (remainingServers.isEmpty()) {
                return new CallbackResult(chatId, messageId,
                        """
                                ✅ Сервер удален!

                                📭 Больше нет доступных серверов.""", null, true);
            }

            String message = String.format(
                    """
                            ✅ *Сервер удален:* %s (ID: %d)

                            🗑️ *Оставшиеся серверы:*

                            Нажмите ❌ Удалить рядом с сервером, который хотите удалить.""",
                    server.getName(), serverId);

            InlineKeyboardMarkup keyboard = keyboardFactory.createServerListWithRemoveButtons(remainingServers);
            return new CallbackResult(chatId, messageId, message, keyboard, true);

        } catch (NumberFormatException e) {
            logService.saveError("Неверный формат ID сервера: " + serverIdStr, e);
            return new CallbackResult(chatId, messageId, "❌ Неверный формат ID сервера.", null, true);
        }
    }

    private String validateServerName(String name) {
        // Проверка на null или пустую строку
        if (name == null || name.trim().isEmpty()) {
            return "❌ Имя сервера не может быть пустым.";
        }

        name = name.trim();

        // Проверка на длину (Hetzner: от 2 до 64 символов)
        if (name.length() < 2) {
            return "❌ Имя сервера должно содержать минимум 2 символа.";
        }

        if (name.length() > 64) {
            return "❌ Имя сервера не может превышать 64 символа.";
        }

        // Первый символ должен быть буквой или цифрой
        char firstChar = name.charAt(0);
        if (!Character.isLetterOrDigit(firstChar)) {
            return "❌ Имя сервера должно начинаться с буквы или цифры.";
        }

        // Последний символ не может быть дефисом или точкой
        char lastChar = name.charAt(name.length() - 1);
        if (lastChar == '-' || lastChar == '.') {
            return "❌ Имя сервера не может заканчиваться на '-' или '.'.";
        }

        // Проверка допустимых символов: буквы, цифры, минусы, точки
        if (!name.matches("^[A-Za-z0-9][A-Za-z0-9\\-\\.]*[A-Za-z0-9]$")) {
            return "❌ Имя сервера может содержать только буквы (A-Z), цифры (0-9), минусы (-) и точки (.).";
        }

        // Проверка на уникальность
        if (serverService.findByName(name) != null) {
            return String.format("❌ Сервер с именем '%s' уже существует.", name);
        }

        return null; // Валидация пройдена
    }

    @Builder
    public record MessageResult(Long chatId, String text, InlineKeyboardMarkup keyboard){}

    @Builder
    public record CallbackResult (Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard, boolean editMessage) {}
}
