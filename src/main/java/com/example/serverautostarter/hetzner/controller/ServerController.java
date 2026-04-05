package com.example.serverautostarter.hetzner.controller;

import com.example.serverautostarter.hetzner.controller.dto.ScriptDto;
import com.example.serverautostarter.hetzner.db.pojo.ServerPojo;
import com.example.serverautostarter.hetzner.controller.dto.ServerDto;
import com.example.serverautostarter.hetzner.service.ServerManager;
import com.example.serverautostarter.hetzner.service.ServerProvisioner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ServerController {

    ServerManager serverManager;
    ServerProvisioner serverProvisioner;

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

    @PostMapping("/test-scripts")
    public void runTestScripts(@RequestBody ScriptDto scriptDto) {
        serverProvisioner.runTestScripts(scriptDto.getIp(), scriptDto.getRootPass());
    }

    @PostMapping("/reset-root-pass")
    public void runTestScripts(@RequestBody ServerDto serverDto) {
        serverManager.resetRootPasswordServer(serverDto.getHetznerId());
    }
}
