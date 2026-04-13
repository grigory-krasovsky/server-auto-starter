package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;

import java.util.List;

public interface ServerService {
    Server saveServerData(ServerPojo serverPojo);
    Server findByHetznerId(Long id);
    List<Server> findAllUncompleted();
    void deleteById(Long id);
    void updateServer(Server server);
}
