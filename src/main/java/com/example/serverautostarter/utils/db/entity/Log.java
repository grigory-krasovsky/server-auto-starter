package com.example.serverautostarter.utils.db.entity;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import com.example.serverautostarter.utils.enums.LogLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Table(name = "logs")
@Entity
@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Log extends AbstractEntity {
    String message;
    String stackTrace;
    String thread;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    LogLevel level;
}
