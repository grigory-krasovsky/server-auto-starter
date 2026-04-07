package com.example.serverautostarter.common.service.impl;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;
import com.example.serverautostarter.common.service.SshService;
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
    public Map<CommandRequestDto, CommandResultDto> runInitialScripts(String ip, String rootPass, List<CommandRequestDto> commands) {

        Map<CommandRequestDto, CommandResultDto> resultMap = new HashMap<>();

        try {
            connect(ip, "root", rootPass);

            for (CommandRequestDto request : commands) {
                resultMap.put(request, executeCommand(request));
            }
        } catch (JSchException e) {
            throw new RuntimeException(String.format("Не удалось присоединиться к серверу %s", ip), e);
        } finally {
            disconnect();
        }
        return resultMap;
    }

    private void connect(String host, String username, String password) throws JSchException {
        JSch jsch = new JSch();
        session = jsch.getSession(username, host, 22);
        session.setPassword(password);

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect(30000);
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
