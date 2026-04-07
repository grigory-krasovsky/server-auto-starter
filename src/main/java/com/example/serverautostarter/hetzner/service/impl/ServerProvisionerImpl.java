package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.dto.CommandResultDto;
import com.example.serverautostarter.common.service.SshService;
import com.example.serverautostarter.common.service.impl.SshServiceImpl;
import com.example.serverautostarter.hetzner.enums.ServerCommands;
import com.example.serverautostarter.hetzner.enums.ServerStatus;
import com.example.serverautostarter.hetzner.service.ServerProvisioner;
import com.example.serverautostarter.utils.service.LogService;
import com.example.serverautostarter.utils.service.PasswordManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.serverautostarter.common.service.SshService.DEFAULT_TIMEOUT_SECONDS;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServerProvisionerImpl implements ServerProvisioner {

    LogService logService;
    PasswordManager passwordManager;
    @Override
    public void runInitialScripts(String ip, String pass) {

        List<CommandRequestDto> commands = Arrays.stream(ServerCommands.values())
                .sorted(Comparator.comparingInt(ServerCommands::getOrder))
                .map(CommandRequestDto::from).toList();

        SshService sshService = new SshServiceImpl();
        Map<CommandRequestDto, CommandResultDto> commandToResult = sshService.runScripts(ip, pass, passwordManager.getAmneziaPass(), commands, logService);
    }

    @Override
    public void runScripts(String ip, String rootPass, ServerStatus initialStatus) {

    }

    @Override
    public void runTestScripts(String ip, String pass) {

        List<CommandRequestDto> commands = new ArrayList<String>() {{
            add("mkdir test");
            add("cd test");
            add("echo 'Hello World' > hello.txt");
            add("cat hello.txt");
        }}.stream().map(command -> CommandRequestDto.builder()
                .script(command)
                .timeout(DEFAULT_TIMEOUT_SECONDS)
                .description("test")
                .build()).collect(Collectors.toList());

        SshService sshService = new SshServiceImpl();
        sshService.runScripts(ip, pass, passwordManager.getAmneziaPass(), commands, logService);
    }
}
