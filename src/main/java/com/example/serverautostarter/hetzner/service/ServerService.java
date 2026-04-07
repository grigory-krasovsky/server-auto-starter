package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;

public interface ServerService {
    Server saveServerData(ServerPojo serverPojo);
}
