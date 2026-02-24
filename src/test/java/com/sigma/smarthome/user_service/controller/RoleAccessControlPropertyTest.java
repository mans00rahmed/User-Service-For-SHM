package com.sigma.smarthome.user_service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleAccessControlPropertyTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

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

        // Current API returns: {"accessToken":"...","tokenType":"Bearer"}
        JsonNode tokenNode = json.get("accessToken");
        if (tokenNode == null) tokenNode = json.get("token"); // fallback if contract changes

        if (tokenNode == null || tokenNode.asText().isBlank()) {
            throw new AssertionError("Login response missing accessToken/token. Body=" + body);
        }

        return tokenNode.asText();
    }

    @Test
    void propertyFeature_returns401_whenNoToken() throws Exception {
        mockMvc.perform(post("/property/feature"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void propertyFeature_returns201_whenPropertyManagerToken() throws Exception {
        String email = "pm_" + System.nanoTime() + "@example.com";
        String token = registerAndLogin(email, "Password123!", UserRole.PROPERTY_MANAGER);

        mockMvc.perform(post("/property/feature")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    void propertyFeature_returns403_whenMaintenanceStaffToken() throws Exception {
        String email = "ms_" + System.nanoTime() + "@example.com";
        String token = registerAndLogin(email, "Password123!", UserRole.MAINTENANCE_STAFF);

        mockMvc.perform(post("/property/feature")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}