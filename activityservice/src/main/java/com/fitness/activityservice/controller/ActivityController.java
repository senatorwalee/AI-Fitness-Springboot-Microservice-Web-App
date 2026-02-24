package com.fitness.activityservice.controller;

import com.fitness.activityservice.dtos.ActivityRequest;
import com.fitness.activityservice.dtos.ActivityResponse;
import com.fitness.activityservice.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for activity-related endpoints.
 * Handles tracking, retrieval, and listing of user activities.
 */
@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    /**
     * Service for activity business logic and persistence.
     */
    private final ActivityService activityService;

    /**
     * Constructor for dependency injection of ActivityService.
     * @param activityService ActivityService instance
     */
    public ActivityController(ActivityService activityService){
        this.activityService = activityService;
    }

    /**
     * Tracks a new activity for a user.
     * @param request ActivityRequest containing activity details
     * @return ResponseEntity containing the tracked activity response
     */
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request){
        return ResponseEntity.ok(activityService.trackActivity(request));
    }

    /**
     * Retrieves a specific activity by its ID.
     * @param activityId the ID of the activity
     * @return ResponseEntity containing the activity response
     */
    @GetMapping("/getActivity/{activityId}")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }

    /**
     * Retrieves all activities for a user.
     * @param userId the ID of the user
     * @return ResponseEntity containing a list of activity responses
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@PathVariable String userId){
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }
}
