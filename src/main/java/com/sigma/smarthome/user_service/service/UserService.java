package com.sigma.smarthome.user_service.service;

import org.springframework.stereotype.Service;

import com.sigma.smarthome.user_service.dto.RegisterRequest;
import com.sigma.smarthome.user_service.dto.RegisterResponse;
import com.sigma.smarthome.user_service.entity.User;
import com.sigma.smarthome.user_service.exception.EmailAlreadyExistsException;
import com.sigma.smarthome.user_service.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public RegisterResponse register(RegisterRequest request) {
		
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new EmailAlreadyExistsException("Email already exists");
		}
		
		User user = new User(request.getEmail(), request.getPassword());
		User saved = userRepository.save(user);
		
		return new RegisterResponse(saved.getId(), saved.getEmail());
		
	}

}
