package com.example.serverautostarter.hetzner.controller.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ScriptDto {
    String ip;
    String rootPass;
}
