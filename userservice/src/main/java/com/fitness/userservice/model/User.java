package com.fitness.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class representing a user in the system.
 * Maps to the 'users' table in the database.
 */
@Entity
@Table(name= "users")
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
public class User {
    /**
     * Unique identifier for the user (UUID).
     */
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
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
     * The user's email address. Must be unique and not null.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The user's hashed password. Must not be null.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Timestamp when the user was created (automatically set).
     */
    @CreationTimestamp
    private LocalDateTime createdAt ;

    /**
     * Timestamp when the user was last updated (automatically set).
     */
    @UpdateTimestamp
    private  LocalDateTime updatedAt;

    /**
     * The user's role (e.g., USER, ADMIN). Defaults to USER.
     */
    @Enumerated(EnumType.STRING)
    private UserRole role  = UserRole.USER;
}
