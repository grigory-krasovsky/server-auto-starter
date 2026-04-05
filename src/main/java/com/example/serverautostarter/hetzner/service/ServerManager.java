package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.db.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.dto.ServerDto;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServersResponse;

public interface ServerManager {
    ServersResponse listAllServers();
    Boolean createNewServer(ServerPojo serverPojo);
    Boolean deleteServer(Long id);
}
