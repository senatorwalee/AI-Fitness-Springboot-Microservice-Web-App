package com.fitness.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway route configuration class.
 * Defines routes programmatically to forward requests to downstream microservices.
 */
@Configuration
public class GatewayConfig {

    /**
     * Configures custom routes for the API Gateway.
     * Routes requests to the appropriate microservices based on path predicates.
     *
     * @param builder the RouteLocatorBuilder used to build routes
     * @return RouteLocator containing all configured routes
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Route for User Service
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://USER-SERVICE"))
                // Route for Activity Service
                .route("activity-service", r -> r
                        .path("/api/activities/**")
                        .uri("lb://ACTIVITY-SERVICE"))
                // Route for AI Service
                .route("ai-service", r -> r
                        .path("/api/ai/**")
                        .uri("lb://AI-SERVICE"))
                .build();
    }
}

