package com.sigma.smarthome.user_service.controller;

import com.sigma.smarthome.user_service.dto.UserResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String testUserService() {
        return "User Service is working";
    }

    @GetMapping("/{id}/role")
    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    public ResponseEntity<Map<String, String>> getUserRole(@PathVariable String id) {
        String role = userService.getRoleById(id).name();
        return ResponseEntity.ok(Map.of("role", role));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String userId = authentication.getName();
        User user = userService.getById(userId);

        return ResponseEntity.ok(new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole()
        ));
    }
}