package com.example.serverautostarter.utils.service;

public interface LogService {

    void saveInfo(String message);
    void saveWarn(String message);
    void saveError(String message, Throwable e);

}
