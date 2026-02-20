package com.fitness.userservice.controller;

import com.fitness.userservice.dtos.RegisterRequest;
import com.fitness.userservice.dtos.UserResponse;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user-related endpoints.
 * Handles user registration and profile retrieval.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    /**
     * Retrieves the profile of a user by their user ID.
     * @param userId the UUID of the user
     * @return ResponseEntity containing the user's profile information
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable UUID userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    /**
     * Registers a new user with the provided registration request.
     * @param request the registration request containing user details
     * @return ResponseEntity containing the registered user's information
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }
}
