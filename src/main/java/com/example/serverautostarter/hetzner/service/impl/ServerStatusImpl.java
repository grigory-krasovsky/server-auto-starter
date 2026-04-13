package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import com.example.serverautostarter.hetzner.db.repository.ServerStatusJpaRepository;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.pojo.ServerStatusPojo;
import com.example.serverautostarter.hetzner.service.ServerStatusService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ServerStatusImpl implements ServerStatusService {

    ServerStatusJpaRepository serverStatusJpaRepository;

    @Override
    public ServerStatus saveStatus(ServerStatusPojo serverStatusPojo, Server server) {
        return serverStatusJpaRepository.save(ServerStatusPojo.toEntity(serverStatusPojo, server));
    }

    @Override
    public ServerStatus getServerCurrentStatus(Server server) {
        List<ServerStatus> allByServer = serverStatusJpaRepository.findAllByServer(server);
        return allByServer.stream().max(Comparator.comparing(ServerStatus::getCreated)).orElse(null);
    }

    @Override
    public void deleteByServer(Server server) {
        serverStatusJpaRepository.deleteByServer(server);
    }
}
