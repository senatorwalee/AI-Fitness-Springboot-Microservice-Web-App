package com.fitness.activityservice.repository;

import com.fitness.activityservice.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repository interface for Activity entities.
 * Extends MongoRepository to provide CRUD operations for activities.
 */
public interface ActivityRepository extends MongoRepository<Activity, String> {
    /**
     * Finds activities by userId (String).
     * @param userId the user ID
     * @return list of activities for the user
     */
    List<Activity> findByUserId(String userId);
}
