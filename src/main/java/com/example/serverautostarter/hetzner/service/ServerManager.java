package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.db.pojo.ServerPojo;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServersResponse;

public interface ServerManager {
    ServersResponse listAllServers();
    Boolean createNewServer(ServerPojo serverPojo);
    Boolean deleteServer(Long id);
    Boolean resetRootPasswordServer(Long serverId);
}
