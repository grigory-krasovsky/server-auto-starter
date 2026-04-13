package com.example.serverautostarter.common.dto;

import com.example.serverautostarter.hetzner.enums.ServerStatusEnum;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandResultDto {
    int exitCode;
    String output;
    String error;
    ServerStatusEnum targetStatus;
}
