package com.sigma.smarthome.user_service.dto;

import com.sigma.smarthome.user_service.enums.UserRole;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String email;
    private UserRole role;

    public UserResponse(UUID id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
}
