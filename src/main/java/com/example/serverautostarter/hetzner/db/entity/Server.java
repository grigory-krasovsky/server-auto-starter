package com.example.serverautostarter.hetzner.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "server")
public class Server {
    @Id
    Long id;
    String name;
    String ip;
    LocalDateTime created;
    Boolean blocked;
    String rootPassEncrypted;
    Long hetznerId;

}
