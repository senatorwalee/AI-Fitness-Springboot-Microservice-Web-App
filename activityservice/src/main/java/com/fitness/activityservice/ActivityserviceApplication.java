package com.fitness.activityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Activity Service Spring Boot application.
 * Starts the application context and runs the service.
 */
@SpringBootApplication
public class ActivityserviceApplication {
    /**
     * Main method to launch the Activity Service application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ActivityserviceApplication.class, args);
    }
}
