package com.fitness.userservice.repository;

import com.fitness.userservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * Extends JpaRepository to provide CRUD operations and custom queries for User objects.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Checks if a user exists with the given email address.
     * @param email the email to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their unique user ID.
     * @param userId the UUID of the user
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findById(UUID userId); //this is optional

}
