package com.fitness.userservice.service;

import com.fitness.userservice.dtos.RegisterRequest;
import com.fitness.userservice.dtos.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for user-related business logic.
 * Handles user registration and profile retrieval.
 */
@Service
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for dependency injection of repository and password encoder.
     * @param repository UserRepository instance
     * @param passwordEncoder PasswordEncoder instance
     */
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the provided registration request.
     * Hashes the password before saving the user.
     * @param request RegisterRequest containing user details
     * @return UserResponse with registered user information
     */
    public UserResponse register( RegisterRequest request) {

        if(repository.existsByEmail(request.getEmail())){
                throw new RuntimeException("Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash the password
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        // Save the user to the database (you can use a repository for this)
        User savedUser = repository.save(user);
        UserResponse userResponse = new UserResponse();
        userResponse.setId(savedUser.getId());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setCreatedAt(savedUser.getCreatedAt());
        userResponse.setUpdatedAt(savedUser.getUpdatedAt());
        return userResponse;

    }

    /**
     * Retrieves the profile of a user by their user ID.
     * @param userId UUID of the user
     * @return UserResponse with user profile information
     */
    public  UserResponse getUserProfile(UUID userId) {
        User user =  repository.findById(userId)
                .orElseThrow(()-> new RuntimeException ("user cannot be found"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;

    }

    public boolean existByUserId(UUID userId){
        log.info("Calling User Validation API for userId: {}", userId);
        return repository.existsById(userId);
    }

}

