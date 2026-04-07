package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.common.service.SshService;
import com.example.serverautostarter.common.service.impl.SshServiceImpl;
import com.example.serverautostarter.hetzner.enums.ServerCommands;
import com.example.serverautostarter.hetzner.service.ServerProvisioner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.serverautostarter.common.service.SshService.DEFAULT_TIMEOUT_SECONDS;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServerProvisionerImpl implements ServerProvisioner {
    @Override
    public void runInitialScripts(String ip, String pass) {

        List<CommandRequestDto> commands = Arrays.stream(ServerCommands.values())
                .sorted(Comparator.comparingInt(ServerCommands::getOrder))
                .map(CommandRequestDto::from).toList();

        SshService sshService = new SshServiceImpl();
        sshService.runInitialScripts(ip, pass, commands);
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
        sshService.runInitialScripts(ip, pass, commands);
    }
}
