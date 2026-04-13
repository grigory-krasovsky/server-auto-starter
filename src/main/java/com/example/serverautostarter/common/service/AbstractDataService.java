package com.example.serverautostarter.common.service;

import com.example.serverautostarter.common.db.entity.AbstractEntity;

public interface AbstractDataService<E extends AbstractEntity> {
    E findById(Long id);
    void deleteById(Long id);
}
