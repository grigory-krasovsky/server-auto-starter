package com.example.serverautostarter.enums;


import lombok.Getter;

public enum OsType {
    UBUNTU_24_04("ubuntu-24.04");

    @Getter
    private final String code;

    OsType(String code) {
        this.code = code;
    }
}
