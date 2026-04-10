package com.sigma.smarthome.user_service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.enums.UserRole;
import com.sigma.smarthome.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserValidationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    private String registerAndLogin(String email, String password, UserRole role) throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword(password);
        reg.setRole(role);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail(email);
        login.setPassword(password);

        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        return json.get("accessToken").asText();
    }

    @Test
    void getUserRole_existingUser_returns200AndRole() throws Exception {
        String managerEmail = "manager_" + System.nanoTime() + "@example.com";
        String targetEmail = "staff_" + System.nanoTime() + "@example.com";
        String password = "Password123!";

        // 1) login as PROPERTY_MANAGER
        String token = registerAndLogin(managerEmail, password, UserRole.PROPERTY_MANAGER);

        // 2) create separate MAINTENANCE_STAFF user
        RegisterRequest target = new RegisterRequest();
        target.setEmail(targetEmail);
        target.setPassword(password);
        target.setRole(UserRole.MAINTENANCE_STAFF);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(target)))
                .andExpect(status().isCreated());

        User savedTarget = userRepository.findByEmail(targetEmail.toLowerCase()).orElseThrow();

        // 3) manager asks for target user's role
        mockMvc.perform(get("/api/v1/users/" + savedTarget.getId() + "/role")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MAINTENANCE_STAFF"));
    }

    @Test
    void getUserRole_nonExistingUser_returns404() throws Exception {
        String email = "rolecheck_missing_" + System.nanoTime() + "@example.com";
        String password = "Password123!";

        String token = registerAndLogin(email, password, UserRole.PROPERTY_MANAGER);

        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/users/" + randomId + "/role")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}