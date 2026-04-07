package com.example.serverautostarter.hetzner.db.pojo;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ServerPojo {
    Long id;
    Long hetznerId;
    String ip;
    String name;
    Boolean blocked;
    String passDecrypted;

    public static ServerPojo from(ServerRequestDto serverRequestDto) {
        return ServerPojo.builder()
                .name(serverRequestDto.getName())
                .build();
    }
}
