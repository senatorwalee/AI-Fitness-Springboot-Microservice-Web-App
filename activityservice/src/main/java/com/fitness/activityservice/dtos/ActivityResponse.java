package com.fitness.activityservice.dtos;

import com.fitness.activityservice.model.ActivityType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for activity response payloads.
 * Encapsulates the data returned to clients after activity operations.
 */
@Data
public class ActivityResponse {
    /**
     * Unique identifier for the tracked activity.
     */
    private String id;
    /**
     * Identifier for the user who performed the activity.
     */
    private String userId;
    /**
     * Type of activity performed.
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
     * Additional metrics related to the activity.
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
