package com.sigma.smarthome.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProtectedController {

    @GetMapping("/protected/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
