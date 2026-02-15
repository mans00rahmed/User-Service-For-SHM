package com.sigma.smarthome.user_service.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
		
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input data");
		
	}
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
	            "error", "UNAUTHORIZED",
	            "message", ex.getMessage()
	    ));
	}


}
