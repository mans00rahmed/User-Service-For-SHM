package com.sigma.smarthome.user_service.entity;

import java.time.Instant;

import com.sigma.smarthome.user_service.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "users_email", columnNames = "email")})
public class User {
	
	@Id
	@Column(nullable = false, updatable = false)
	private UUID id;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;
	
	public User() {}
	
	public User(String email, String password, UserRole role) {
		this.email = email;
		this.password = password;
		this.role = role;
	}
	
	@PrePersist
	protected void onCreate() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
		
		if (this.createdAt == null) {
			this.createdAt = Instant.now();
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UUID getId() {
		return id;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

}
