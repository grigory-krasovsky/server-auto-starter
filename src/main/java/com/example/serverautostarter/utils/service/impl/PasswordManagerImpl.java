package com.example.serverautostarter.utils.service.impl;

import com.example.serverautostarter.utils.service.LogService;
import com.example.serverautostarter.utils.service.PasswordManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordManagerImpl implements PasswordManager {

    private final LogService logService;

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    @Value("${ENCRYPTION_KEY:MySuperSecretKey123}")
    private String ENCRYPTION_KEY;
    @Value("${AMNEZIA_PASS}")
    private String AMNEZIA_PASS;

    @Override
    public String encrypt(String pass) {
        try {
            SecretKeySpec keySpec = getKeySpec();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(pass.getBytes());

            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            logService.saveError("Ошибка во время кодировки", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(String encryptedPass) {
        byte[] combined = Base64.getDecoder().decode(encryptedPass);

        byte[] iv = new byte[16];
        byte[] encrypted = new byte[combined.length - 16];
        System.arraycopy(combined, 0, iv, 0, 16);
        System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

        SecretKeySpec keySpec = getKeySpec();
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(TRANSFORMATION);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logService.saveError("Ошибка во время раскодировки 1", e);
            throw new RuntimeException(e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            logService.saveError("Ошибка во время раскодировки 2", e);
            throw new RuntimeException(e);
        }

        byte[] decrypted;
        try {
            decrypted = cipher.doFinal(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logService.saveError("Ошибка во время раскодировки 3", e);
            throw new RuntimeException(e);
        }
        return new String(decrypted);
    }

    @Override
    public String getAmneziaPass() {
        return AMNEZIA_PASS;
    }

    private SecretKeySpec getKeySpec() {

        byte[] keyBytes = ENCRYPTION_KEY.getBytes();
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logService.saveError("Алгоритм %s не найден", e);
            throw new RuntimeException(e);
        }
        keyBytes = messageDigest.digest(keyBytes);
        keyBytes = java.util.Arrays.copyOf(keyBytes, 32);

        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}
