package com.example.serverautostarter.utils.service.impl;

import com.example.serverautostarter.utils.service.PasswordManager;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class PasswordManagerImplTest {

    @Autowired
    private PasswordManager passwordManager;

    @Test
    void shouldReturnSameString() {
        String pass = "testPass123";
        String encrypted = passwordManager.encrypt(pass);
        String decrypted = passwordManager.decrypt(encrypted);

        assertEquals(pass, decrypted);
    }

    @Test
    void shouldProduceDifferentEncryptedValues() {
        String original = "testPassword";

        String encrypted1 = passwordManager.encrypt(original);
        String encrypted2 = passwordManager.encrypt(original);

        assertNotEquals(encrypted1, encrypted2);
    }
}
