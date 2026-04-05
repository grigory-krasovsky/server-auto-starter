package com.example.serverautostarter.hetzner.db.pojo;

import com.example.serverautostarter.hetzner.controller.dto.ServerDto;
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
    LocalDateTime created;

    public static ServerPojo from(ServerDto serverDto) {
        return ServerPojo.builder()
                .name(serverDto.getName())
                .build();
    }
}
