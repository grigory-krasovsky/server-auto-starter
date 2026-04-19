package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;

import java.util.List;

public interface ServerService {
    Server saveServerData(ServerPojo serverPojo);
    Server findByHetznerId(Long id);
    Server findById(Long id);
    Server findByName(String name);
    List<Server> findAllUncompleted();
    List<Server> findAll();
    void deleteById(Long id);
    void updateServer(Server server);
}
