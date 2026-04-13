package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.common.dto.CommandRequestDto;
import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import com.example.serverautostarter.hetzner.enums.ServerCommands;
import com.example.serverautostarter.hetzner.enums.ServerStatusEnum;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.pojo.ServerStatusPojo;
import com.example.serverautostarter.hetzner.service.ServerManager;
import com.example.serverautostarter.hetzner.service.ServerProvisioner;
import com.example.serverautostarter.hetzner.service.ServerService;
import com.example.serverautostarter.hetzner.service.ServerStatusService;
import com.example.serverautostarter.utils.service.LogService;
import com.example.serverautostarter.utils.service.PasswordManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static com.example.serverautostarter.hetzner.enums.ServerCommands.ROOT_PASS_CHANGING;
import static com.example.serverautostarter.hetzner.enums.ServerStatusEnum.READY_FOR_INITIAL_SCRIPTS;
import static com.example.serverautostarter.hetzner.enums.ServerStatusEnum.WAITING_FOR_ROOT_PASS_CHANGE;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@EnableScheduling
public class ServerTasksExecutor {

    ServerStatusService serverStatusService;
    ServerService serverService;
    ServerProvisioner serverProvisioner;
    ServerManager serverManager;
    LogService logger;
    PasswordManager passwordManager;


    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.SECONDS)
    public void processServers() {
        List<Server> allUncompleted = serverService.findAllUncompleted().stream().toList();
        allUncompleted.forEach(this::processStage);
    }

    @Transactional
    protected void processStage(Server server) {
        ServerStatus serverCurrentStatus = serverStatusService.getServerCurrentStatus(server);
        ServerCommands commandForNextStep = ServerCommands.getCommandForNextStep(serverCurrentStatus.getStatus());
        if (commandForNextStep == null) {
            return;
        }
        ServerStatusEnum newStatus;
        if (commandForNextStep.equals(ROOT_PASS_CHANGING)) {
            try {

                Boolean passResetComplete = Optional.of(serverManager.resetRootPassword(server.getHetznerId()))
                        .filter(Predicate.not(String::isBlank))
                        .map(newPass -> {
                            if (serverProvisioner.connectSuccessful(server.getIp(), newPass)) {
                                server.setRootPassEncrypted(passwordManager.encrypt(newPass));
                                serverService.updateServer(server);
                                logger.saveInfo(String.format("Выполнена смена root пароля для сервера %s. Текущий статус сервера: %s", server.getIp(), READY_FOR_INITIAL_SCRIPTS));
                                return true;
                            }
                            return false;
                        }).orElse(false);

                newStatus = passResetComplete ? READY_FOR_INITIAL_SCRIPTS : WAITING_FOR_ROOT_PASS_CHANGE;
            } catch (Exception e) {
                logger.saveError(String.format("Ошибка во время попытки сменить root пароль для сервера %s", server.getIp()), e);
                throw e;
            }
        } else {
            try {
                serverProvisioner.runSingleScript(server.getIp(), Optional.ofNullable(server.getRootPassEncrypted()).map(passwordManager::decrypt).orElse(null), CommandRequestDto.from(commandForNextStep));
                logger.saveInfo(String.format("Выполнена команда для сервера %s. Текущий статус сервера: %s", server.getIp(), commandForNextStep.getServerDesiredStatus()));
                newStatus = commandForNextStep.getServerDesiredStatus();
            } catch (Exception e) {
                logger.saveError(String.format("Ошибка во время выполнения команды для сервера %s. Желаемый статус %s. Текущий статус %s",
                        server.getIp(),
                        commandForNextStep.getServerDesiredStatus(),serverCurrentStatus.getStatus()), e);
                throw e;
            }
        }
        serverStatusService.saveStatus(ServerStatusPojo.from(newStatus), server);
    }
}
