package com.example.serverautostarter.hetzner.service.impl;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.repository.ServerJpaRepository;
import com.example.serverautostarter.hetzner.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.service.ServerService;
import com.example.serverautostarter.utils.service.PasswordManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ServerServiceImpl implements ServerService {

    ServerJpaRepository serverJpaRepository;
    PasswordManager passwordManager;

    @Override
    public Server saveServerData(ServerPojo serverPojo) {
        serverPojo.setPassEncrypted(passwordManager.encrypt(serverPojo.getPassDecrypted()));
        serverPojo.setPassDecrypted(null);
        return serverJpaRepository.save(ServerPojo.toEntity(serverPojo));
    }
}
