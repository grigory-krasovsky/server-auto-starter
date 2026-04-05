package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.enums.OsType;
import com.example.serverautostarter.enums.ServerType;
import com.example.serverautostarter.hetzner.db.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.service.ServerManager;
import io.github.sinuscosinustan.hetznercloud.HetznerCloudAPI;
import io.github.sinuscosinustan.hetznercloud.objects.request.CreateServerRequest;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServersResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServerManagerImpl implements ServerManager {

    HetznerCloudAPI hetznerCloudAPI;

    @Override
    public ServersResponse listAllServers() {
        return hetznerCloudAPI.getServers();
    }

    @Override
    public Boolean createNewServer(ServerPojo serverPojo) {
        try {
            hetznerCloudAPI.createServer(CreateServerRequest.builder()
                    .name(serverPojo.getName())
                    .serverType(ServerType.CX_23.getCode())
                    .image(OsType.UBUNTU_24_04.getCode())
                    .startAfterCreate(true)
                    .build());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean deleteServer(Long id) {
        try {
            hetznerCloudAPI.deleteServer(id);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
