package com.fitness.aiservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Recommendation entity representing AI-generated fitness activity recommendations.
 * Stores detailed analysis and suggestions for a specific activity.
 * Maps to the 'recommendations' collection in MongoDB.
 */
@Document(collection = "recommendations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {
    /**
     * Unique identifier for the recommendation.
     */
    @Id
    private String id;

    /**
     * Reference to the activity ID that this recommendation is for.
     */
    private String activityId;

    /**
     * Reference to the user ID who performed the activity.
     */
    private String userId;

    /**
     * Type of activity (e.g., CYCLING, RUNNING).
     */
    private String activityType;

    /**
     * Overall analysis feedback and summary from the AI.
     * Contains combined feedback and summary of the activity performance.
     */
    private String recommendation;

    /**
     * List of specific areas for improvement with detailed recommendations.
     */
    private List<String> improvements;

    /**
     * List of suggested workout types and descriptions for future training.
     */
    private List<String> suggestions;

    /**
     * List of safety instructions and tips for the activity.
     */
    private List<String> safety;

    /**
     * Timestamp when the recommendation was created.
     */
    @CreatedDate
    private LocalDateTime createdAt;

}
