package com.sigma.smarthome.user_service.service;

import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.enums.UserRole;
import com.sigma.smarthome.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServicePasswordHashTest {

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    
    
    @Test
    void registerUser_withoutRole_assignsDefaultRole() {
        String email = "taha_role_" + System.currentTimeMillis() + "@test.com";

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword("Password123!");
        // role intentionally not set

        userService.register(req);

        User saved = userRepository.findByEmail(email)
                .orElseThrow(() -> new AssertionError("User not saved"));

        assertEquals(UserRole.MAINTENANCE_STAFF, saved.getRole());
    }

    @Test
    void registerUser_savesHashedPassword_notPlainText() {

        String email = "taha_hash_" + System.currentTimeMillis() + "@test.com";
        String plainPassword = "Password123!";

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(plainPassword);
        req.setRole(UserRole.PROPERTY_MANAGER);

        userService.register(req);

        User saved = userRepository.findByEmail(email)
                .orElseThrow(() -> new AssertionError("User not saved"));

        assertNotEquals(plainPassword, saved.getPassword(), "Password should be hashed, not plain text");
        assertTrue(passwordEncoder.matches(plainPassword, saved.getPassword()), "Hashed password should match original");
    }
}
