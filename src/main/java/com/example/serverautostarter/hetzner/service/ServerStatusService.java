package com.example.serverautostarter.hetzner.service;


import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import com.example.serverautostarter.hetzner.pojo.ServerStatusPojo;

public interface ServerStatusService {
    ServerStatus saveStatus(ServerStatusPojo serverStatusPojo, Server server);
}
