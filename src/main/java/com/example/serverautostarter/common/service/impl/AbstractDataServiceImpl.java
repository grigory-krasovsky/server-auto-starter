package com.example.serverautostarter.common.service.impl;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import com.example.serverautostarter.common.repository.AbstractDataRepository;
import com.example.serverautostarter.common.service.AbstractDataService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AbstractDataServiceImpl implements AbstractDataService<AbstractEntity> {

    AbstractDataRepository abstractDataRepository;
    @Override
    public AbstractEntity findById(Long id) {
        return abstractDataRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        abstractDataRepository.deleteById(id);
    }
}
