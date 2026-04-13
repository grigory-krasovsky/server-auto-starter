package com.example.serverautostarter.hetzner.db.repository;

import com.example.serverautostarter.hetzner.db.entity.Server;
import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerStatusJpaRepository extends JpaRepository<ServerStatus, Long> {
    List<ServerStatus> findAllByServer(Server server);

    void deleteByServer(Server server);
}
