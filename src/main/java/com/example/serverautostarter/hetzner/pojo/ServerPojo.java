package com.example.serverautostarter.hetzner.pojo;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import com.example.serverautostarter.hetzner.db.entity.Server;
import io.github.sinuscosinustan.hetznercloud.objects.response.CreateServerResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServerPojo {
    Long id;
    Long hetznerId;
    String ip;
    String name;
    Boolean blocked;
    String passDecrypted;
    String passEncrypted;

    public static ServerPojo from(ServerRequestDto serverRequestDto) {
        return ServerPojo.builder()
                .name(serverRequestDto.getName())
                .build();
    }

    public static Server toEntity(ServerPojo serverPojo) {
        return Server.builder()
                .ip(serverPojo.getIp())
                .name(serverPojo.getName())
                .hetznerId(serverPojo.getHetznerId())
                .blocked(serverPojo.getBlocked())
                .created(LocalDateTime.now())
                .rootPassEncrypted(serverPojo.getPassEncrypted())
                .build();
    }

    public static ServerPojo from(Server server) {
        return ServerPojo.builder()
                .ip(server.getIp())
                .name(server.getName())
                .hetznerId(server.getHetznerId())
                .blocked(server.getBlocked())
                .passEncrypted(server.getRootPassEncrypted())
                .build();
    }

    public static ServerPojo from(CreateServerResponse serverResponse) {
        io.github.sinuscosinustan.hetznercloud.objects.general.Server hetznerServer = serverResponse.getServer();
        return ServerPojo.builder()
                .ip(hetznerServer.getPublicNet().getIpv4().getIp())
                .name(hetznerServer.getName())
                .hetznerId(hetznerServer.getId())
                .blocked(false)
                .passDecrypted(serverResponse.getRootPassword())
                .build();
    }
}
