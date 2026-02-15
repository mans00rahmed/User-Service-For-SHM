package com.sigma.smarthome.user_service.service;

import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.dto.RegisterResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.enums.UserRole;
import com.sigma.smarthome.user_service.exception.EmailAlreadyExistsException;
import com.sigma.smarthome.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getById(String userId) {
        UUID id = UUID.fromString(userId);
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    public RegisterResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserRole role = (request.getRole() != null)
                ? request.getRole()
                : UserRole.MAINTENANCE_STAFF;

        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash

        User saved = userRepository.save(user);

        return new RegisterResponse(saved.getId(), saved.getEmail());
    }
}
