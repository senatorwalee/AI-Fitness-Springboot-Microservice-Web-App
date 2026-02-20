package com.fitness.userservice.dtos;

import com.fitness.userservice.model.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
