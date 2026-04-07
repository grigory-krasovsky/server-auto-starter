package com.example.serverautostarter.hetzner.db.repository;

import com.example.serverautostarter.hetzner.db.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerJpaRepository extends JpaRepository<Server, Long> {
}
