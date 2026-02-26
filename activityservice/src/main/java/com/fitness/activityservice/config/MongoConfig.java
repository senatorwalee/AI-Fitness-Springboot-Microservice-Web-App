package com.fitness.activityservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Configuration class for MongoDB settings in the Activity Service.
 * Customizes MongoDB client and mapping configuration if needed.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
