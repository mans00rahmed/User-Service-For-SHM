package com.sigma.smarthome.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private String registerAndLoginGetToken(String email, UserRole role) throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword("Password123!");
        reg.setRole(role);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword("Password123!");

        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body).get("accessToken").asText();
    }

    @Test
    void getCurrentUser_withValidToken_returns200() throws Exception {
        String token = registerAndLoginGetToken(
                "me_" + System.nanoTime() + "@test.com",
                UserRole.PROPERTY_MANAGER
        );

        mockMvc.perform(get("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.role").value("PROPERTY_MANAGER"));
    }

    @Test
    void getCurrentUser_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserRole_withValidToken_returns200() throws Exception {
        String email = "role_" + System.nanoTime() + "@test.com";
        String token = registerAndLoginGetToken(email, UserRole.MAINTENANCE_STAFF);

        // Get user ID from /me endpoint
        String meBody = mockMvc.perform(get("/api/v1/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = objectMapper.readTree(meBody).get("id").asText();

        mockMvc.perform(get("/api/v1/users/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MAINTENANCE_STAFF"));
    }

    @Test
    void getUserRole_withInvalidId_returns404() throws Exception {
        String token = registerAndLoginGetToken(
                "role404_" + System.nanoTime() + "@test.com",
                UserRole.PROPERTY_MANAGER
        );

        mockMvc.perform(get("/api/v1/users/00000000-0000-0000-0000-000000000000/role")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}