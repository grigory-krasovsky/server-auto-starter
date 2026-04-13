package com.example.serverautostarter.hetzner.db.entity;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import com.example.serverautostarter.hetzner.enums.ServerStatusEnum;
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
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "TEXT", name = "status")
    ServerStatusEnum status;
    String error;
}
