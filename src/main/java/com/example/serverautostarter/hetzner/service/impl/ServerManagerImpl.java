package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.hetzner.enums.OsType;
import com.example.serverautostarter.hetzner.enums.ServerType;
import com.example.serverautostarter.hetzner.db.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.service.ServerManager;
import com.example.serverautostarter.hetzner.service.ServerProvisioner;
import io.github.sinuscosinustan.hetznercloud.HetznerCloudAPI;
import io.github.sinuscosinustan.hetznercloud.objects.request.CreateServerRequest;
import io.github.sinuscosinustan.hetznercloud.objects.response.CreateServerResponse;
import io.github.sinuscosinustan.hetznercloud.objects.response.ResetRootPasswordResponse;
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
    ServerProvisioner serverProvisioner;

    @Override
    public ServersResponse listAllServers() {
        return hetznerCloudAPI.getServers();
    }

    @Override
    public Boolean createNewServer(ServerPojo serverPojo) {
        try {
            CreateServerResponse server = hetznerCloudAPI.createServer(CreateServerRequest.builder()
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

    @Override
    public Boolean resetRootPasswordServer(Long serverId) {
        try {
            ResetRootPasswordResponse resetRootPasswordResponse = hetznerCloudAPI.resetRootPassword(serverId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
