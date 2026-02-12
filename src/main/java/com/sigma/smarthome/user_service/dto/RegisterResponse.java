package com.sigma.smarthome.user_service.dto;

public class RegisterResponse {
	
	private Long id;
	private String email;
	
	public RegisterResponse() {}
	
	public RegisterResponse(Long id, String email) {
		this.id = id;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
