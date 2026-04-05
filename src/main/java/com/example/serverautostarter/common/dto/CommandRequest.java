package com.example.serverautostarter.common.dto;

import com.example.serverautostarter.hetzner.enums.ServerCommands;
import com.example.serverautostarter.hetzner.enums.ServerStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandRequest {
    @NotNull
    String script;
    @NotNull
    Integer timeout;
    String description;
    ServerStatus desiredStatus;

    public static CommandRequest from(ServerCommands serverCommands) {
        return CommandRequest.builder()
                .script(serverCommands.getScript())
                .desiredStatus(serverCommands.getServerDesiredStatus())
                .description(serverCommands.getServerDesiredStatus().getDescription())
                .timeout(serverCommands.getTimeout())
                .build();
    }
}
