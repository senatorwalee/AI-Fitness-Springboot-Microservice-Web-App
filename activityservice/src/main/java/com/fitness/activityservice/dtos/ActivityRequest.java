package com.fitness.activityservice.dtos;

import com.fitness.activityservice.model.ActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for activity tracking requests.
 * Contains the necessary fields for tracking a new activity.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityRequest {
    /**
     * Identifier for the user performing the activity.
     */
    private String userId; // we can also get this from the header
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
}
