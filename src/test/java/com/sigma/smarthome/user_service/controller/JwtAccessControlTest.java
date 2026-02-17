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
class JwtAccessControlTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    private static final String PASS = "Password123!";

    private String registerAndLoginGetToken(String email, String password) throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail(email);
        reg.setPassword(password);
        reg.setRole(UserRole.PROPERTY_MANAGER);

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
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(body).get("accessToken").asText();
    }

    @Test
    void validLogin_returns200_andJwtToken() throws Exception {
        registerAndLoginGetToken("valid@login.com", PASS);
    }

    @Test
    void invalidLogin_returns401() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setEmail("wrongpass@login.com");
        reg.setPassword(PASS);
        reg.setRole(UserRole.PROPERTY_MANAGER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest();
        login.setEmail("wrongpass@login.com");
        login.setPassword("WRONG_PASSWORD");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validJwt_allowsProtectedEndpoint_returns200() throws Exception {
        String token = registerAndLoginGetToken("jwtok@login.com", PASS);

        mockMvc.perform(get("/protected/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    void missingJwt_deniesProtectedEndpoint_returns401() throws Exception {
        mockMvc.perform(get("/protected/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidJwt_deniesProtectedEndpoint_returns401() throws Exception {
        mockMvc.perform(get("/protected/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }
}
