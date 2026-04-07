package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import com.example.serverautostarter.hetzner.db.repository.ServerStatusJpaRepository;
import com.example.serverautostarter.hetzner.pojo.ServerStatusPojo;
import com.example.serverautostarter.hetzner.service.ServerStatusService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ServerStatusImpl implements ServerStatusService {

    ServerStatusJpaRepository serverStatusJpaRepository;
    @Override
    public ServerStatus saveStatus(ServerStatusPojo serverStatusPojo, Server server) {
        return serverStatusJpaRepository.save(ServerStatusPojo.toEntity(serverStatusPojo, server));
    }
}
