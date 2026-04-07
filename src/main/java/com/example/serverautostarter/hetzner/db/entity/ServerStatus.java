package com.example.serverautostarter.hetzner.db.entity;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "server_status")
@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
public class ServerStatus extends AbstractEntity {
    @ManyToOne
    Server server;
    Boolean creationInitiated;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT")
    com.example.serverautostarter.hetzner.enums.ServerStatus status;
    String error;
}
