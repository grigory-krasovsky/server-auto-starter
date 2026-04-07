package com.example.serverautostarter.utils.service.impl;

import com.example.serverautostarter.utils.db.entity.Log;
import com.example.serverautostarter.utils.db.repository.LogJpaRepository;
import com.example.serverautostarter.utils.enums.LogLevel;
import com.example.serverautostarter.utils.service.LogService;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LogServiceImpl implements LogService {
    LogJpaRepository logJpaRepository;

    @Override
    public void saveInfo(String message) {

        save(Log.builder()
                .message(message)
                .build(), LogLevel.INFO);
    }

    @Override
    public void saveWarn(String message) {
        save(Log.builder()
                .message(message)
                .build(), LogLevel.WARN);
    }

    @Override
    public void saveError(String message, Throwable e) {
        save(Log.builder()
                .message(message)
                .stackTrace(getStackTrace(e))
                .build(), LogLevel.ERROR);
    }

    @Override
    public void saveError(String message) {
        save(Log.builder()
                .message(message)
                .build(), LogLevel.ERROR);
    }

    private void save(Log log, LogLevel level) {
        log.setLevel(level);
        log.setThread(Thread.currentThread().getName());
        log.setCreated(LocalDateTime.now());
        logJpaRepository.save(log);
    }

    private static String getStackTrace(Throwable throwable) {
        return Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
    }
}
