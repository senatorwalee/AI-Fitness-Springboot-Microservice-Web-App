package com.fitness.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class for the application.
 * Defines beans for password encoding and HTTP security settings.
 */
@Configuration
public class SecurityConfig {
    /**
     * Bean for password encoding using BCrypt with strength 12.
     * @return PasswordEncoder instance
     */
    @Bean
     public  PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder(12);
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * Disables CSRF and permits all requests (for development/demo purposes).
     * @param http HttpSecurity object
     * @return SecurityFilterChain instance
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }


}
