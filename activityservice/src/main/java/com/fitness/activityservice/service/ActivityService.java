package com.fitness.activityservice.service;

import com.fitness.activityservice.dtos.ActivityRequest;
import com.fitness.activityservice.dtos.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service class for handling business logic related to activities.
 * Provides methods for creating, retrieving, and managing activity records.
 */
@Service
@RequiredArgsConstructor
public class ActivityService {
    /**
     * Logger for activity service operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);
    /**
     * Repository for activity persistence.
     */
    private final ActivityRepository activityRepository;

    private final UserValidationService userValidationService;

    /**
     * Tracks a new activity for a user.
     * @param request ActivityRequest containing activity details
     * @return ActivityResponse containing tracked activity information
     */
    public ActivityResponse trackActivity(ActivityRequest request){
        logger.info("Received activity tracking request: {}", request);
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("User " + request.getUserId() + " is not a valid user");
        }
        Activity activity = Activity.builder()
                .userId(request.getUserId()) // userId is now String
                .type(request.getType())
                .duration(request.getDuration())
                .startTime(request.getStartTime())
                .caloriesBurned(request.getCaloriesBurned())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        logger.info("Saving activity to database: {}", activity);
        Activity savedActivity = activityRepository.save(activity);
        logger.info("Activity saved with id: {}", savedActivity.getId());
        return  mapToResponse(savedActivity);
    }

    /**
     * Maps an Activity entity to an ActivityResponse DTO.
     * @param activity Activity entity
     * @return ActivityResponse DTO
     */
    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration((activity.getDuration()));
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setUpdatedAt(activity.getUpdatedAt());
        response.setCreatedAt(activity.getCreatedAt());
        return response;
    }

    /**
     * Retrieves all activities for a user.
     * @param userId the user ID
     * @return list of ActivityResponse for the user
     */
    public List<ActivityResponse> getUserActivities(String userId){
        List<Activity> activities = activityRepository.findByUserId(userId);
        return activities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a specific activity by its ID.
     * @param activityId the activity ID
     * @return ActivityResponse for the activity
     * @throws RuntimeException if activity not found
     */
    public ActivityResponse getActivityById(String activityId){
        return activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(()-> new RuntimeException("Cannot find activity with id " + activityId));
    }
}
