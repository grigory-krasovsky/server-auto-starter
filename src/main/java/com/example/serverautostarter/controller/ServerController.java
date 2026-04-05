package com.example.serverautostarter.controller;

import com.example.serverautostarter.hetzner.db.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.dto.ServerDto;
import com.example.serverautostarter.hetzner.service.ServerManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ServerController {

    ServerManager serverManager;

    @GetMapping("/servers")
    public void getServers() {
        serverManager.listAllServers();
    }

    @PostMapping("/servers")
    public void createServer(@RequestBody ServerDto serverDto) {
        serverManager.createNewServer(ServerPojo.from(serverDto));
    }

    @DeleteMapping("/servers/{id}")
    public void deleteServer(@PathVariable Long id) {
        serverManager.deleteServer(id);
    }
}
