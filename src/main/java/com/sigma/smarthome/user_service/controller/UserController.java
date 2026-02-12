package com.sigma.smarthome.user_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {


    private final RestTemplate restTemplate;

    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
}
