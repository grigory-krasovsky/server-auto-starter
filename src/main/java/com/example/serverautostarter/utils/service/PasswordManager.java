package com.example.serverautostarter.utils.service;

public interface PasswordManager {
    String encrypt(String pass);
    String decrypt(String pass);
}
