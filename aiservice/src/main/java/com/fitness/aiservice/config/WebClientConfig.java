package com.fitness.aiservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient beans in the AI Service.
 * Provides a WebClient.Builder bean for dependency injection.
 */
@Configuration
public class WebClientConfig {
    /**
     * Creates a WebClient.Builder bean for making HTTP requests.
     * @return a WebClient.Builder instance
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}

