package com.sigma.smarthome.user_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordConfigTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoderBean_isCreated() {
        assertNotNull(passwordEncoder);
    }

    @Test
    void passwordEncoder_hashesAndMatches() {
        String raw = "Password123!";
        String hash = passwordEncoder.encode(raw);

        assertNotNull(hash);
        assertNotEquals(raw, hash);                 // should be hashed (different)
        assertTrue(passwordEncoder.matches(raw, hash)); // should match
    }
}
