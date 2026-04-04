package com.sigma.smarthome.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property")
public class PropertyFeatureController {

    @PreAuthorize("hasRole('PROPERTY_MANAGER')")
    @PostMapping("/feature")
    public ResponseEntity<String> createPropertyFeature() {
        System.out.println(">>> HIT PropertyFeatureController");
        return ResponseEntity.status(HttpStatus.CREATED).body("Property feature created");
    }
}