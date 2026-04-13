package com.example.serverautostarter.hetzner.service;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;
import io.github.sinuscosinustan.hetznercloud.objects.response.CreateServerResponse;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServerResponse;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServersResponse;

public interface ServerManager {
    ServersResponse listAllServers();
    ServerResponse findServerById(Long hetznerId);
    void createNewServer(ServerRequestDto serverRequestDto);
    CreateServerResponse newServerCommand(ServerRequestDto serverRequestDto);
    Boolean deleteServer(Long id);
    String resetRootPassword(Long serverId);

    Boolean isServerReady(Long hetznerId);
}
