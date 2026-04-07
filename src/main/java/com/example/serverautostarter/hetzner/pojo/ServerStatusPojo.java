package com.example.serverautostarter.hetzner.pojo;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.enums.ServerStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerStatusPojo {
    Boolean creationInitiated;
    ServerStatus status;
    String error;
    Long serverId;

    public static ServerStatusPojo from(ServerStatus serverStatusEnum) {
        return ServerStatusPojo.builder()
                .status(serverStatusEnum)
                .build();
    }

    public static com.example.serverautostarter.hetzner.db.entity.ServerStatus toEntity(ServerStatusPojo serverStatusPojo, Server server) {
        return com.example.serverautostarter.hetzner.db.entity.ServerStatus.builder()
                .status(serverStatusPojo.getStatus())
                .server(server)
                .created(LocalDateTime.now())
                .build();
    }
}
