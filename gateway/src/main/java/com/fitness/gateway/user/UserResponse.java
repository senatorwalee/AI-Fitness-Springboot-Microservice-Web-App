package com.fitness.gateway.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for sending user information in responses.
 * This class is used to transfer user data from the backend to the client.
 */
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
public class UserResponse {
    /**
     * Unique identifier for the user.
     */
    private UUID id;
    /**
     * Unique identifier for the user in keycloak.
     */
    private String keyCloakId;
    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The user's email address.
     */
    private String email;

    /**
     * Timestamp when the user was created.
     */
    private LocalDateTime createdAt ;
    /**
     * Timestamp when the user was last updated.
     */
    private  LocalDateTime updatedAt;

}
