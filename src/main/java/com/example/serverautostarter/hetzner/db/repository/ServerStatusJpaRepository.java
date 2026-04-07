package com.example.serverautostarter.hetzner.db.repository;

import com.example.serverautostarter.hetzner.db.entity.ServerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerStatusJpaRepository extends JpaRepository<ServerStatus, Long> {
}
