package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.hetzner.controller.dto.ServerRequestDto;
import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.enums.OsType;
import com.example.serverautostarter.hetzner.enums.ServerStatusEnum;
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
import io.github.sinuscosinustan.hetznercloud.objects.response.ServerResponse;
import io.github.sinuscosinustan.hetznercloud.objects.response.ServersResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public ServerResponse findServerById(Long hetznerId) {
        return hetznerCloudAPI.getServer(hetznerId);
    }

    @Override
    @Transactional
    public void createNewServer(ServerRequestDto serverRequestDto) {
        CreateServerResponse response = null;
        try {
            response = newServerCommand(serverRequestDto);
            saveMetaData(response);
        } catch (Exception e) {
            logService.saveError("Ошибка во время попытки создания нового сервера.", e);
            deleteServer(Optional.ofNullable(response).map(CreateServerResponse::getServer)
                    .map(io.github.sinuscosinustan.hetznercloud.objects.general.Server::getId)
                    .orElse(null));
            throw new RuntimeException();
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

        Server newServer = serverService.saveServerData(serverPojo);
        logService.saveInfo(String.format("Метаданные для сервера с названием %s и ip %s сохранены в таблицу server", serverPojo.getName(), serverPojo.getIp()));

        serverStatusService.saveStatus(ServerStatusPojo.from(ServerStatusEnum.WAITING_FOR_ROOT_PASS_CHANGE), newServer);
        logService.saveInfo(String.format("Серверу %s присвоен статус '%s'", serverPojo.getName(), ServerStatusEnum.WAITING_FOR_ROOT_PASS_CHANGE.getDescription()));
    }

    @Override
    @Transactional
    public Boolean deleteServer(Long id) {
        try {
            Optional.ofNullable(serverService.findById(id))
                    .ifPresent((server) -> {
                        hetznerCloudAPI.deleteServer(server.getHetznerId());
                        serverStatusService.deleteByServer(server);
                        serverService.deleteById(server.getId());
                    });
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String resetRootPassword(Long serverId) {
        if (isServerReady(serverId)) {
            try {
                ResetRootPasswordResponse resetRootPasswordResponse = hetznerCloudAPI.resetRootPassword(serverId);
                return resetRootPasswordResponse.getRootPassword();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    @Override
    public Boolean isServerReady(Long hetznerId) {
        ServerResponse server = findServerById(hetznerId);
        return server.getServer().getStatus().equals("running");
    }
}
