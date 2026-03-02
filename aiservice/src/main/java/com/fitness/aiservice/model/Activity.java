package com.fitness.aiservice.model;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity class representing an activity record in the system.
 * Contains details such as userId, type, duration, calories burned, and additional metrics.
 * Maps to the 'activities' collection in MongoDB.
 */
@Data
public class Activity {
    /**
     * Unique identifier for the activity.
     */
    private String id;
    /**
     * Identifier for the user who performed the activity (String).
     */
    private String userId;
    /**
     * Type of activity performed (e.g., RUNNING, CYCLING).
     */
    private ActivityType type;
    /**
     * Duration of the activity in minutes.
     */
    private Integer duration;
    /**
     * Calories burned during the activity.
     */
    private Integer caloriesBurned;
    /**
     * Start time of the activity.
     */
    private LocalDateTime startTime;
    /**
     * Additional metrics related to the activity (e.g., distance, steps).
     */

    private Map<String, Object> additionalMetrics;
    /**
     * Timestamp when the activity was created.
     */
    private LocalDateTime createdAt;
    /**
     * Timestamp when the activity was last updated.
     */

    private LocalDateTime updatedAt;
}
