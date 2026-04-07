package com.example.serverautostarter.utils.db.repository;

import com.example.serverautostarter.utils.db.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogJpaRepository extends JpaRepository<Log, Long> {
}
