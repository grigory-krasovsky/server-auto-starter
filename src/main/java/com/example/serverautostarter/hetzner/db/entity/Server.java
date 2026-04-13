package com.example.serverautostarter.hetzner.db.entity;

import com.example.serverautostarter.common.db.entity.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "server")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Server extends AbstractEntity {
    String name;
    String ip;
    Boolean blocked;
    String rootPassEncrypted;
    Long hetznerId;
    Boolean initializationCompleted;
}
