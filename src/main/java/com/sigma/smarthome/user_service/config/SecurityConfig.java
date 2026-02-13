package com.sigma.smarthome.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfig {

    private static final RequestMatcher H2_MATCHER = new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher("/h2-console"),
            PathPatternRequestMatcher.withDefaults().matcher("/h2-console/**")
    );

    @Bean
    @Order(0)
    SecurityFilterChain h2ConsoleSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(H2_MATCHER)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.ignoringRequestMatchers(H2_MATCHER))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    @Order(1)
    SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // backup whitelist (so even if chain-0 doesn't match, this won't block it)
                .requestMatchers("/h2-console", "/h2-console/**").permitAll()

                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
