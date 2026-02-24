package com.fitness.activityservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity class representing a user activity.
 * Maps to the 'activities' collection in MongoDB.
 */
@Document(collection = "activities")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Field("metrics")
    private Map<String, Object> additionalMetrics;
    /**
     * Timestamp when the activity was created.
     */
    @CreatedDate
    private LocalDateTime createdAt;
    /**
     * Timestamp when the activity was last updated.
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
