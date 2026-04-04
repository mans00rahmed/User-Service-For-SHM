package com.sigma.smarthome.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.enums.UserRole;
import com.sigma.smarthome.user_service.security.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JwtPayloadStructureTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired JwtService jwtService;

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
    void jwtToken_containsUserId_asSubject() throws Exception {
        String token = registerAndLoginGetToken(
                "payload_userid_" + System.nanoTime() + "@test.com",
                UserRole.PROPERTY_MANAGER
        );

        Claims claims = jwtService.parseClaims(token);

        assertNotNull(claims.getSubject(), "Subject (user_id) should not be null");
        assertFalse(claims.getSubject().isBlank(), "Subject (user_id) should not be blank");
    }

    @Test
    void jwtToken_containsRole_claim() throws Exception {
        String token = registerAndLoginGetToken(
                "payload_role_" + System.nanoTime() + "@test.com",
                UserRole.PROPERTY_MANAGER
        );

        Claims claims = jwtService.parseClaims(token);
        String role = claims.get("role", String.class);

        assertNotNull(role, "Role claim should not be null");
        assertEquals("PROPERTY_MANAGER", role);
    }

    @Test
    void jwtToken_containsEmail_claim() throws Exception {
        String email = "payload_email_" + System.nanoTime() + "@test.com";
        String token = registerAndLoginGetToken(email, UserRole.PROPERTY_MANAGER);

        Claims claims = jwtService.parseClaims(token);
        String emailClaim = claims.get("email", String.class);

        assertNotNull(emailClaim, "Email claim should not be null");
        assertEquals(email, emailClaim);
    }

    @Test
    void jwtToken_containsRole_forMaintenanceStaff() throws Exception {
        String token = registerAndLoginGetToken(
                "payload_staff_" + System.nanoTime() + "@test.com",
                UserRole.MAINTENANCE_STAFF
        );

        Claims claims = jwtService.parseClaims(token);
        String role = claims.get("role", String.class);

        assertNotNull(role, "Role claim should not be null");
        assertEquals("MAINTENANCE_STAFF", role);
    }

    @Test
    void jwtToken_isNotExpired_afterGeneration() throws Exception {
        String token = registerAndLoginGetToken(
                "payload_expiry_" + System.nanoTime() + "@test.com",
                UserRole.PROPERTY_MANAGER
        );

        Claims claims = jwtService.parseClaims(token);

        assertNotNull(claims.getExpiration(), "Expiration should not be null");
        assertTrue(claims.getExpiration().after(new java.util.Date()), "Token should not be expired");
    }
}