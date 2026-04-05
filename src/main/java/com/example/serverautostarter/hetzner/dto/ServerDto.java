package com.example.serverautostarter.hetzner.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ServerDto {

    @NonNull
    String name;

    Long hetznerId;
}
