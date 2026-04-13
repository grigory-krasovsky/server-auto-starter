package com.example.serverautostarter.hetzner.pojo;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.enums.ServerStatusEnum;
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
    ServerStatusEnum status;
    String error;
    Long serverId;

    public static ServerStatusPojo from(ServerStatusEnum serverStatusEnum) {
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
