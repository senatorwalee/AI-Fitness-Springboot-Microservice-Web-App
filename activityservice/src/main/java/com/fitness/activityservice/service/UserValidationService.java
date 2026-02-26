package com.fitness.activityservice.service;

import com.fitness.activityservice.config.WebClientConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;

/**
 * Service for validating users by communicating with the User Service.
 * Uses a WebClient to send validation requests and handle responses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    /**
     * WebClient instance configured for communicating with the User Service.
     */
    private final WebClient userServiceWebClient;

    /**
     * Validates if a user exists and is valid by calling the User Service.
     *
     * @param userId the UUID of the user to validate
     * @return true if the user is valid, false otherwise
     * @throws RuntimeException if the user is not found, the request is invalid, or the service is unavailable
     */
    public boolean validateUser(String userId){
        log.info("Calling User Validation API for userId: {}", userId);
        try{
            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        }catch (WebClientResponseException e){
            if(e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw  new RuntimeException("User Not Found: "  + userId);
            else if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw  new RuntimeException("Invalid request: "  + userId);
        } catch (WebClientException e ){
            throw  new RuntimeException("User service is unavailable please try again later", e);
        }

        return false;
    }
}
