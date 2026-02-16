package com.sigma.smarthome.user_service.controller;

import com.sigma.smarthome.user_service.dto.LoginRequest;
import com.sigma.smarthome.user_service.dto.LoginResponse;
import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.dto.RegisterResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.security.JwtService;
import com.sigma.smarthome.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse res = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        User user;
        try {
            user = userService.getByEmail(request.getEmail());
        } catch (RuntimeException ex) {
            // email not found -> 401 (matches acceptance criteria)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean ok = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user.getId().toString(), user.getRole());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
