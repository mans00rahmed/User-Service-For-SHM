package com.sigma.smarthome.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // For REST APIs, usually disable CSRF (or at least for auth endpoints)
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                // allow registration without login
                .requestMatchers("/auth/**", "/actuator/health").permitAll()
                // everything else protected (change later if needed)
                .anyRequest().authenticated()
            )

            // keep default basic login for now (later you’ll add JWT)
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
