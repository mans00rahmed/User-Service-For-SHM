package com.sigma.smarthome.user_service.dto;

import java.util.UUID;

public class RegisterResponse {
	
	private UUID id;
	private String email;
	
	public RegisterResponse() {}
	
	public RegisterResponse(UUID id, String email) {
		this.id = id;
		this.email = email;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
