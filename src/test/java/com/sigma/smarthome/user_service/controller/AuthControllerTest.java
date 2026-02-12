package com.sigma.smarthome.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // ✅ Boot 4 import
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.sigma.smarthome.user_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.crypto.bcrypt.BCrypt;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Test
    void register_successful_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("taha_success_" + System.currentTimeMillis() + "@test.com");
        req.setPassword("Password123!");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        String email = "taha_dup_" + System.currentTimeMillis() + "@test.com";

        RegisterRequest first = new RegisterRequest();
        first.setEmail(email);
        first.setPassword("Password123!");

        RegisterRequest second = new RegisterRequest();
        second.setEmail(email);
        second.setPassword("Password123!");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_invalidPassword_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("taha_badpass_" + System.currentTimeMillis() + "@test.com");
        req.setPassword("");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
    
    
    


    @Test
    void register_passwordIsHashed_inDatabase() throws Exception {
        String email = "taha_hash_" + System.currentTimeMillis() + "@test.com";
        String rawPassword = "Password123!";

        RegisterRequest req = new RegisterRequest();
        req.setEmail(email);
        req.setPassword(rawPassword);

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
        ).andExpect(status().isCreated());

        var saved = userRepository.findByEmail(email).orElseThrow();
        Assertions.assertNotEquals(rawPassword, saved.getPassword());         // not plain text
        Assertions.assertTrue(BCrypt.checkpw(rawPassword, saved.getPassword())); // matches bcrypt hash
    }

}
