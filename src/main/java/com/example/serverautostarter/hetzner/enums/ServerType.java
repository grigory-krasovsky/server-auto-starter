package com.example.serverautostarter.hetzner.enums;


import lombok.Getter;

public enum ServerType {
    CX_23("cx23");

    @Getter
    private final String code;

    ServerType(String code) {
        this.code = code;
    }
}
