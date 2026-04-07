package com.example.serverautostarter.utils.service;

import com.example.serverautostarter.utils.pojo.LogPojo;

public interface LogService {

    void saveInfo(String message);
    void saveWarn(String message);
    void saveError(String message, Throwable e);

}
