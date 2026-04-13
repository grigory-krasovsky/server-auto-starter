package com.example.serverautostarter.hetzner.db.repository;

import com.example.serverautostarter.hetzner.db.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServerJpaRepository extends JpaRepository<Server, Long> {
    List<Server> findAllByInitializationCompleted(Boolean completed);
    Server findByHetznerId(Long id);
    void deleteById(Long id);
}
