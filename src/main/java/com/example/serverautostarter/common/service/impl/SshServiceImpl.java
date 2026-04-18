package com.example.serverautostarter.common.service.impl;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;
import com.example.serverautostarter.common.service.SshService;
import com.example.serverautostarter.utils.service.LogService;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
        }
        return true;
    }

    private void connect(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        IntStream.range(1, 4).forEach(attempt -> {
            try {
                session.connect(30000);
            } catch (JSchException | RuntimeException e) {
                System.out.printf("Attempt %s...%n", attempt);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

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
