package com.example.serverautostarter.common.repository;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AbstractDataRepository extends JpaRepository<AbstractEntity, Long> {
    @Override
    Optional<AbstractEntity> findById(Long aLong);

    @Override
    void deleteById(Long aLong);
}
