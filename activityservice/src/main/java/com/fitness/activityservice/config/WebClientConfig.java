package com.fitness.activityservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient beans used in the Activity Service.
 * Provides load-balanced and user-service-specific WebClient instances.
 */
@Configuration
public class WebClientConfig {
    /**
     * Creates a load-balanced WebClient.Builder bean for making HTTP requests to other services.
     *
     * @return a load-balanced WebClient.Builder
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder (){
        return WebClient.builder();
    }

    /**
     * Creates a WebClient bean specifically configured for communicating with the USER-SERVICE.
     *
     * @param webClientBuilder the load-balanced WebClient.Builder
     * @return a WebClient instance with base URL set to USER-SERVICE
     */
    @Bean
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder){
        return  webClientBuilder
                .baseUrl("http://USERSERVICE")
                .build();
    }
}
