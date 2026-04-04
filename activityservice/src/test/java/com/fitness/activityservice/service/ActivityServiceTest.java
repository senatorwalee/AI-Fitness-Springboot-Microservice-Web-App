package com.fitness.activityservice.service;

import com.fitness.activityservice.dtos.ActivityRequest;
import com.fitness.activityservice.dtos.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.model.ActivityType;
import com.fitness.activityservice.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ActivityService business logic.
 * Tests activity tracking, retrieval, and RabbitMQ integration.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService Unit Tests")
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ActivityService activityService;

    private ActivityRequest validRequest;
    private Activity mockActivity;
    private String testUserId;
    private String testActivityId;

    /**
     * Setup test data before each test.
     */
    @BeforeEach
    void setUp() {
        // Initialize test data
        testUserId = "test-user-123";
        testActivityId = "activity-456";

        // Create a valid activity request
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("distance", 10.5);
        metrics.put("avgHeartRate", 140);

        validRequest = new ActivityRequest();
        validRequest.setUserId(testUserId);
        validRequest.setType(ActivityType.RUNNING);
        validRequest.setDuration(45);
        validRequest.setCaloriesBurned(500);
        validRequest.setStartTime(LocalDateTime.now());
        validRequest.setAdditionalMetrics(metrics);

        // Create mock activity entity
        mockActivity = new Activity();
        mockActivity.setId(testActivityId);
        mockActivity.setUserId(testUserId);
        mockActivity.setType(ActivityType.RUNNING);
        mockActivity.setDuration(45);
        mockActivity.setCaloriesBurned(500);
        mockActivity.setStartTime(LocalDateTime.now());
        mockActivity.setAdditionalMetrics(metrics);
        mockActivity.setCreatedAt(LocalDateTime.now());
        mockActivity.setUpdatedAt(LocalDateTime.now());

        // Set private field values using reflection
        ReflectionTestUtils.setField(activityService, "exchangeName", "fitness.exchange");
        ReflectionTestUtils.setField(activityService, "routingKey", "activity.created");
    }

    /**
     * Test: trackActivity with valid user should save and publish to RabbitMQ.
     * Scenario: User is valid, activity is saved, and message is sent to queue.
     */
    @Test
    @DisplayName("Should track activity successfully when user is valid")
    void testTrackActivityWithValidUser() {
        // Arrange
        when(userValidationService.validateUser(testUserId)).thenReturn(true);
        when(activityRepository.save(any(Activity.class))).thenReturn(mockActivity);

        // Act
        ActivityResponse response = activityService.trackActivity(validRequest);

        // Assert
        assertNotNull(response, "ActivityResponse should not be null");
        assertEquals(testActivityId, response.getId(), "Activity ID should match");
        assertEquals(testUserId, response.getUserId(), "User ID should match");
        assertEquals(ActivityType.RUNNING, response.getType(), "Activity type should be RUNNING");
        assertEquals(45, response.getDuration(), "Duration should be 45");
        assertEquals(500, response.getCaloriesBurned(), "Calories burned should be 500");

        // Verify interactions
        verify(userValidationService, times(1)).validateUser(testUserId);
        verify(activityRepository, times(1)).save(any(Activity.class));

        // Verify RabbitMQ publish was called with correct parameters
        ArgumentCaptor<Activity> activityCaptor = ArgumentCaptor.forClass(Activity.class);
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq("fitness.exchange"),
                eq("activity.created"),
                activityCaptor.capture()
        );
        assertEquals(testActivityId, activityCaptor.getValue().getId());
    }

    /**
     * Test: trackActivity with invalid user should throw exception.
     * Scenario: User validation fails, activity should not be saved.
     */
    @Test
    @DisplayName("Should throw exception when user is invalid")
    void testTrackActivityWithInvalidUser() {
        // Arrange
        when(userValidationService.validateUser(testUserId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> activityService.trackActivity(validRequest),
                "Should throw RuntimeException for invalid user"
        );

        assertTrue(exception.getMessage().contains("not a valid user"),
                "Exception message should indicate user is invalid");

        // Verify repository was NOT called
        verify(activityRepository, never()).save(any(Activity.class));
    }

    /**
     * Test: getActivityById should return activity when found.
     * Scenario: Activity exists in repository, correct response is returned.
     */
    @Test
    @DisplayName("Should retrieve activity by ID when activity exists")
    void testGetActivityByIdFound() {
        // Arrange
        when(activityRepository.findById(testActivityId)).thenReturn(Optional.of(mockActivity));

        // Act
        ActivityResponse response = activityService.getActivityById(testActivityId);

        // Assert
        assertNotNull(response, "ActivityResponse should not be null");
        assertEquals(testActivityId, response.getId(), "Activity ID should match");
        assertEquals(testUserId, response.getUserId(), "User ID should match");

        verify(activityRepository, times(1)).findById(testActivityId);
    }

    /**
     * Test: getActivityById should throw exception when activity not found.
     * Scenario: Activity does not exist in repository.
     */
    @Test
    @DisplayName("Should throw exception when activity ID not found")
    void testGetActivityByIdNotFound() {
        // Arrange
        when(activityRepository.findById(testActivityId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> activityService.getActivityById(testActivityId),
                "Should throw RuntimeException when activity not found"
        );

        assertTrue(exception.getMessage().contains("Cannot find activity"),
                "Exception message should indicate activity not found");

        verify(activityRepository, times(1)).findById(testActivityId);
    }
}






