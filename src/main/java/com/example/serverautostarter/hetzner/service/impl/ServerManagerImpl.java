package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.enums.OsType;
import com.example.serverautostarter.hetzner.enums.ServerStatus;
import com.example.serverautostarter.hetzner.enums.ServerType;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.pojo.ServerStatusPojo;
import com.example.serverautostarter.hetzner.service.ServerManager;
import com.example.serverautostarter.hetzner.service.ServerProvisioner;
import com.example.serverautostarter.hetzner.service.ServerService;
import com.example.serverautostarter.hetzner.service.ServerStatusService;
import com.example.serverautostarter.utils.service.LogService;
import com.example.serverautostarter.utils.service.PasswordManager;
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
    LogService logService;
    PasswordManager passwordManager;
    ServerService serverService;
    ServerStatusService serverStatusService;


    @Override
    public ServersResponse listAllServers() {
        return hetznerCloudAPI.getServers();
    }

    @Override
    public boolean createNewServer(ServerRequestDto serverRequestDto) {
        try {
            CreateServerResponse response = newServerCommand(serverRequestDto);
            saveMetaData(response);
            return true;
        } catch (Exception e) {
            logService.saveError("Ошибка во время попытки создания нового сервера.", e);
            return false;
        }
    }

    @Override
    public CreateServerResponse newServerCommand(ServerRequestDto serverRequestDto) {
        try {
            CreateServerResponse response = hetznerCloudAPI.createServer(CreateServerRequest.builder()
                    .name(serverRequestDto.getName())
                    .serverType(ServerType.CX_23.getCode())
                    .image(OsType.UBUNTU_24_04.getCode())
                    .startAfterCreate(true)
                    .build());

            logService.saveInfo(String.format("Новый сервер с названием %s создан. Id %s", serverRequestDto.getName(), response.getServer().getId()));

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveMetaData(CreateServerResponse response) {
        ServerPojo serverPojo = ServerPojo.from(response);

        Server serverMetadata = serverService.saveServerData(serverPojo);
        logService.saveInfo(String.format("Метаданные для сервера с названием %s и ip %s сохранены в таблицу server", serverPojo.getName(), serverPojo.getIp()));

        serverStatusService.saveStatus(ServerStatusPojo.from(ServerStatus.READY_FOR_INITIAL_SCRIPTS), serverMetadata);
        logService.saveInfo(String.format("Серверу %s присвоен статус '%s'", serverPojo.getName(), ServerStatus.READY_FOR_INITIAL_SCRIPTS.getDescription()));
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
    public String resetRootPassword(Long serverId) {
        try {
            ResetRootPasswordResponse resetRootPasswordResponse = hetznerCloudAPI.resetRootPassword(serverId);
            return resetRootPasswordResponse.getRootPassword();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Override
//    public Boolean runDefaultPipeline(ServerRequestDto serverRequestDto) {
//        CreateServerResponse newServer = createNewServer(ServerPojo.from(serverRequestDto));
//        Long serverId = newServer.getServer().getId();
//        logService.saveInfo(String.format("Новый сервер с названием %s создан. Id %s", serverRequestDto.getName(), serverId));
//        String newPass = resetRootPassword(serverId);
//        logService.saveInfo(String.format("Новый сервер с названием %s создан. Id %s", serverRequestDto.getName(), serverId));
//    }
}
