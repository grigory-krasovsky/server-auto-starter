package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;
import io.github.sinuscosinustan.hetznercloud.objects.response.CreateServerResponse;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServersResponse;

public interface ServerManager {
    ServersResponse listAllServers();
    boolean createNewServer(ServerRequestDto serverRequestDto);
    CreateServerResponse newServerCommand(ServerRequestDto serverRequestDto);
    Boolean deleteServer(Long id);
    String resetRootPassword(Long serverId);

//    Boolean runDefaultPipeline(ServerRequestDto serverRequestDto);
}
