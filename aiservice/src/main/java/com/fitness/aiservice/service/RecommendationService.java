package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling recommendation logic in the AI Service.
 * Provides methods to retrieve recommendations for users and activities.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {
    /**
     * Repository for accessing Recommendation data from the database.
     */
    private final RecommendationRepository recommendationRepository;

    /**
     * Retrieves all recommendations for a given user by userId.
     * @param userId the ID of the user
     * @return list of Recommendation objects for the user
     */
    public List<Recommendation> getUserRecommendation(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    /**
     * Retrieves a recommendation for a specific activity by activityId.
     * Throws a 404 error if not found.
     * @param activityId the ID of the activity
     * @return Recommendation object for the activity
     * @throws ResponseStatusException if no recommendation is found
     */
    public Recommendation getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No recommendation found for activity " + activityId)
                );
    }
}
