package com.example.serverautostarter.common.service.impl;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;
import com.example.serverautostarter.common.service.SshService;
import com.example.serverautostarter.utils.service.LogService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SshServiceImpl implements SshService {

    Session session = null;

    @Override
    public Map<CommandRequestDto, CommandResultDto> runScripts(String ip, String rootPass, String amneziaPass, List<CommandRequestDto> commands, LogService logger) {

        Map<CommandRequestDto, CommandResultDto> resultMap = new HashMap<>();

        try {
            connect(ip, "root", rootPass);

            for (CommandRequestDto request : commands) {
                request.setScript(request.getScript().replace("${AMNEZIA_PASS}", amneziaPass));
                CommandResultDto commandResultDto = executeCommand(request);
                resultMap.put(request, commandResultDto);
                if (commandResultDto.getExitCode() == -1) {
                    logger.saveError(String.format("Сервер: %s. Команда не выполнена. Ошибка: %s", ip, commandResultDto.getError()));
                } else {
                    logger.saveInfo(String.format("Сервер: %s. Команда выполнена. Статус: %s", ip, request.getDesiredStatus()));
                }
            }
        } catch (JSchException e) {
            logger.saveError(String.format("Не удалось присоединиться к серверу %s", ip), e);
            throw new RuntimeException(String.format("Не удалось присоединиться к серверу %s", ip), e);
        } finally {
            disconnect();
        }
        return resultMap;
    }

    @Override
    public boolean connectSuccessful(String ip, String rootPass) {
        try {
            connect(ip, "root", rootPass);
        } catch (Exception e) {
            return false;
        } finally {
            disconnect();
        }
        return true;
    }

    private void connect(String host, String username, String password) throws JSchException {
        JSchException lastException = null;

        for (int i = 1; i <= 3; i++) {
            JSch jsch = null;
            Session newSession = null;

            try {
                jsch = new JSch();
                newSession = jsch.getSession(username, host, 22);
                newSession.setPassword(password);

                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                // Добавляем поддержку старых алгоритмов для некоторых серверов
                config.put("kex", "diffie-hellman-group-exchange-sha256,diffie-hellman-group14-sha256,diffie-hellman-group1-sha1");
                newSession.setConfig(config);

                newSession.connect(30000);

                // Успех! Сохраняем сессию
                this.session = newSession;
                System.out.printf("Успешное подключение к %s с попытки %d%n", host, i);
                return;

            } catch (JSchException | RuntimeException e) {
                lastException = e instanceof JSchException ? (JSchException) e : new JSchException(e.getMessage(), e);
                System.err.printf("Попытка %d подключения к %s не удалась: %s%n", i, host, e.getMessage());

                // Закрываем сессию если она была создана
                if (newSession != null && newSession.isConnected()) {
                    newSession.disconnect();
                }

                if (i == 3) {
                    throw new JSchException(String.format("Не удалось подключиться к %s после 3 попыток", host), lastException);
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new JSchException("Прервано ожидание между попытками", ex);
                }
            }
        }
    }

    private void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    private CommandResultDto executeCommand(CommandRequestDto request) {
        ChannelExec channel = null;
        CommandResultDto.CommandResultDtoBuilder commandResultBuilder = CommandResultDto.builder();
        commandResultBuilder.targetStatus(request.getDesiredStatus());


        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(request.getScript());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            channel.setOutputStream(out);
            channel.setErrStream(err);

            channel.connect();

            long deadline = System.currentTimeMillis() + (request.getTimeout() * 1000L);

            while (!channel.isClosed()) {
                if (System.currentTimeMillis() > deadline) {
                    throw new InterruptedException(String.format("Команда %s не выполнена. Выход по таймауту", request.getDescription()));
                }
                Thread.sleep(100);
            }

            int exitCode = channel.getExitStatus();
            return commandResultBuilder
                    .exitCode(exitCode)
                    .output(out.toString())
                    .error(err.toString())
                    .build();

        } catch (JSchException | InterruptedException e) {
            return commandResultBuilder
                    .exitCode(-1)
                    .output("")
                    .error(e.getMessage())
                    .build();
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }
}
