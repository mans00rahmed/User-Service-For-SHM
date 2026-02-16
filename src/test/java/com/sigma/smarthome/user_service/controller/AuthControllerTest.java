package com.sigma.smarthome.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.enums.UserRole;
import com.sigma.smarthome.user_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void register_returns201_whenValidUniqueEmailAndPassword() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("cian@example.com");
        req.setPassword("Password123!");
        req.setRole(UserRole.PROPERTY_MANAGER); // ✅ NEW

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated());
    }

    @Test
    void register_returns409_whenEmailAlreadyExists() throws Exception {
        String email = "duplicate@example.com";

        RegisterRequest first = new RegisterRequest();
        first.setEmail(email);
        first.setPassword("Password123!");
        first.setRole(UserRole.PROPERTY_MANAGER); // ✅ NEW

        RegisterRequest second = new RegisterRequest();
        second.setEmail(email);
        second.setPassword("Password123!");
        second.setRole(UserRole.PROPERTY_MANAGER); // ✅ NEW

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first))
        ).andExpect(status().isCreated());

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second))
        ).andExpect(status().isConflict());
    }

    @Test
    void register_returns400_whenPasswordMissingOrInvalid() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("badpass@example.com");
        req.setPassword(""); // invalid
        req.setRole(UserRole.PROPERTY_MANAGER); // ✅ NEW

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void register_passwordIsHashed_inDatabase() throws Exception {
        String email = "hash@example.com";
        String rawPassword = "Password123!";

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(rawPassword);
        req.setRole(UserRole.PROPERTY_MANAGER); // ✅ NEW

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated());

        var saved = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new AssertionError("User not saved"));

        Assertions.assertNotEquals(rawPassword, saved.getPassword());
        Assertions.assertTrue(BCrypt.checkpw(rawPassword, saved.getPassword()));
    }
    
    @Test
    void login_returns200_andToken_whenValidCredentials() throws Exception {
        // register first
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail("login_ok@example.com");
        reg.setPassword("Password123!");
        reg.setRole(UserRole.PROPERTY_MANAGER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isCreated());

        // login
        LoginRequest login = new LoginRequest();
        login.setEmail("login_ok@example.com");
        login.setPassword("Password123!");

        var result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andReturn();

        String body = result.getResponse().getContentAsString();

        var node = objectMapper.readTree(body);
        String accessToken = node.get("accessToken").asText();

        Assertions.assertNotNull(accessToken);
        Assertions.assertFalse(accessToken.isBlank(), "accessToken should not be blank");
    }

    @Test
    void login_returns401_whenPasswordWrong() throws Exception {
        // register first
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail("login_badpass@example.com");
        reg.setPassword("Password123!");
        reg.setRole(UserRole.MAINTENANCE_STAFF);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isCreated());

        // login with wrong password
        var login = new LoginRequest();
        login.setEmail("login_badpass@example.com");
        login.setPassword("WrongPassword!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void login_returns401_whenEmailNotFound() throws Exception {
        var login = new LoginRequest();
        login.setEmail("missing@example.com");
        login.setPassword("Password123!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }

}
