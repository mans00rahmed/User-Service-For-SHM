package com.sigma.smarthome.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property")
public class PropertyFeatureController {

	// this is a stub for property management feature
	@PostMapping("/feature")
	public ResponseEntity<String> createPropertyFeature(){
	    System.out.println(">>> HIT PropertyFeatureController");
		return ResponseEntity.status(HttpStatus.CREATED).body("Property feature created");
	}
	
}
