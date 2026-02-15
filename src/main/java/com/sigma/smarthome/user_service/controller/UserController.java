package com.sigma.smarthome.user_service.controller;

import com.sigma.smarthome.user_service.dto.UserResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    private final RestTemplate restTemplate;
    private final UserService userService;

    public UserController(RestTemplate restTemplate, UserService userService) {
        this.restTemplate = restTemplate;
        this.userService = userService;
    }

    @GetMapping("/users/property-test")
    public String callPropertyService() {
        return restTemplate.getForObject(
                "http://property-service/properties/test",
                String.class
        );
    }

    @GetMapping("/users/test")
    public String testUserService() {
        return "User Service is working";
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName(); // JWT subject (sub)
        User user = userService.getById(userId);

        return ResponseEntity.ok(
                new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }
}
