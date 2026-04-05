package com.example.serverautostarter.hetzner.db.entity;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "server_status")
@Getter
@Setter
@SuperBuilder
public class ServerStatus extends AbstractEntity {
    @ManyToOne
    Server server;
    Boolean creationInitiated;
    Boolean status;
    String error;
}
