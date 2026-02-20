package com.fitness.userservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Data Transfer Object (DTO) for user registration requests.
 * This class holds the information required to register a new user.
 * Validation annotations ensure that the input data is correct and safe.
 */
@Data // Lombok annotation to generate getters, setters, toString, equals, and hashCode methods
@AllArgsConstructor // Lombok annotation to generate a constructor with all fields
@NoArgsConstructor  // Lombok annotation to generate a no-argument constructor
public class RegisterRequest {
    /**
     * The user's email address.
     * Must not be blank and must be a valid email format.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * The user's password.
     * Must not be blank and must be at least 6 characters long.
     */
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * The user's first name (optional).
     */
    private String firstName;

    /**
     * The user's last name (optional).
     */
    private String lastName;
}
