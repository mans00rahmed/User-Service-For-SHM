package com.sigma.smarthome.user_service.service;

import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.dto.RegisterResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.exception.EmailAlreadyExistsException;
import com.sigma.smarthome.user_service.exception.InvalidCredentialsException;
import com.sigma.smarthome.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    
    public void login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        boolean ok = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!ok) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    public RegisterResponse register(RegisterRequest request) {
    	
    	String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setRole(request.getRole());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ hash

        User saved = userRepository.save(user);

        return new RegisterResponse(saved.getId(), saved.getEmail());
    }
}
