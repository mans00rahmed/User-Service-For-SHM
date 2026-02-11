package com.sigma.smarthome.maintenance_service.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/users/test")
    public String testUserService() {
        return "User Service is working";
    }
}
