package com.sigma.smarthome.user_service.controller;

import com.sigma.smarthome.user_service.dto.UserResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.enums.UserRole;
import com.sigma.smarthome.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();

        User user = userService.getById(userId);

        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/role")
    public ResponseEntity<Map<String, String>> getUserRole(@PathVariable String id) {
        UserRole role = userService.getRoleById(id);
        return ResponseEntity.ok(Map.of("role", role.name()));
    }
}