package com.example.serverautostarter.common.dto;

import com.example.serverautostarter.hetzner.enums.ServerStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommandResult {
    int exitCode;
    String output;
    String error;
    ServerStatus targetStatus;
}
