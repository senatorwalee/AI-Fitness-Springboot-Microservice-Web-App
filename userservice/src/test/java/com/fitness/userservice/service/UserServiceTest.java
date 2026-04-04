package com.fitness.userservice.service;

import com.fitness.userservice.dtos.RegisterRequest;
import com.fitness.userservice.dtos.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService business logic.
 * Tests user registration, profile retrieval, and password encoding.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;
    private User mockUser;
    private UUID testUserId;
    private String testEmail;
    private String testPassword;
    private String hashedPassword;

    /**
     * Setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Initialize test data
        testUserId = UUID.randomUUID();
        testEmail = "testuser@gmail.com";
        testPassword = "SecurePassword123!";
        hashedPassword = "$2a$10$hashedPasswordExample123456789";

        // Create a register request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail(testEmail);
        registerRequest.setPassword(testPassword);
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setKeyCloakId(testUserId);

        // Create mock user entity
        mockUser = new User();
        mockUser.setId(testUserId);
        mockUser.setEmail(testEmail);
        mockUser.setPassword(hashedPassword);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setKeyCloakId(testUserId);
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Test: register new user should save and hash password.
     * Scenario: Email does not exist, user is created with hashed password.
     */
    @Test
    @DisplayName("Should register new user with hashed password")
    void testRegisterNewUser() {
        // Arrange
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response, "UserResponse should not be null");
        assertEquals(testUserId, response.getId(), "User ID should match");
        assertEquals(testEmail, response.getEmail(), "Email should match");
        assertEquals("John", response.getFirstName(), "First name should match");
        assertEquals("Doe", response.getLastName(), "Last name should match");

        // Verify password was encoded
        verify(passwordEncoder, times(1)).encode(testPassword);
        // Verify user was saved
        verify(userRepository, times(1)).save(any(User.class));
    }

    /**
     * Test: register existing user should return existing user without saving.
     * Scenario: Email already exists in repository.
     */
    @Test
    @DisplayName("Should return existing user when email already registered")
    void testRegisterExistingUser() {
        // Arrange
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);
        when(userRepository.findByEmail(testEmail)).thenReturn(mockUser);

        // Act
        UserResponse response = userService.register(registerRequest);

        // Assert
        assertNotNull(response, "UserResponse should not be null");
        assertEquals(testUserId, response.getId(), "User ID should match");
        assertEquals(testEmail, response.getEmail(), "Email should match");

        // Verify password encoder and save were NOT called for existing user
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        // Verify we only checked and found the user
        verify(userRepository, times(1)).existsByEmail(testEmail);
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    /**
     * Test: getUserProfile should return user when found.
     * Scenario: User exists in repository, correct profile is returned.
     */
    @Test
    @DisplayName("Should retrieve user profile by ID when user exists")
    void testGetUserProfileFound() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(mockUser));

        // Act
        UserResponse response = userService.getUserProfile(testUserId);

        // Assert
        assertNotNull(response, "UserResponse should not be null");
        assertEquals(testUserId, response.getId(), "User ID should match");
        assertEquals(testEmail, response.getEmail(), "Email should match");
        assertEquals("John", response.getFirstName(), "First name should match");

        verify(userRepository, times(1)).findById(testUserId);
    }

    /**
     * Test: getUserProfile should throw exception when user not found.
     * Scenario: User ID does not exist in repository.
     */
    @Test
    @DisplayName("Should throw exception when user profile not found")
    void testGetUserProfileNotFound() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUserProfile(testUserId),
                "Should throw RuntimeException when user not found"
        );

        assertTrue(exception.getMessage().contains("user cannot be found"),
                "Exception message should indicate user not found");

        verify(userRepository, times(1)).findById(testUserId);
    }

    /**
     * Test: existByUserId should return true/false based on keycloak ID.
     * Scenario: Check if user exists by their KeyCloak ID.
     */
    @Test
    @DisplayName("Should verify user existence by KeyCloak ID")
    void testExistByUserId() {
        // Arrange - User exists
        when(userRepository.existsByKeyCloakId(testUserId)).thenReturn(true);

        // Act
        boolean exists = userService.existByUserId(testUserId);

        // Assert
        assertTrue(exists, "User should exist");
        verify(userRepository, times(1)).existsByKeyCloakId(testUserId);

        // Arrange - User does not exist
        reset(userRepository);
        when(userRepository.existsByKeyCloakId(testUserId)).thenReturn(false);

        // Act
        exists = userService.existByUserId(testUserId);

        // Assert
        assertFalse(exists, "User should not exist");
        verify(userRepository, times(1)).existsByKeyCloakId(testUserId);
    }
}


