package com.fitness.gateway.user;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Service for validating users by communicating with the User Service.
 * Uses a WebClient to send validation requests and handle responses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    /**
     * WebClient instance configured for communicating with the User Service.
     */
    private final WebClient userServiceWebClient;

    /**
     * Validates if a user exists and is valid by calling the User Service.
     *
     * @param userId the UUID of the user to validate
     * @return Mono<Boolean> that emits true if the user is valid, false otherwise
     */
    public Mono<Boolean> validateUser(String userId){
        log.info("Calling User Validation API for userId: {}", userId);

        return userServiceWebClient.get()
                .uri("/api/users/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(new RuntimeException("User not found: " + userId));
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("invalid request"));
                    }
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });
    }

    public RegisterRequest getUserDetails(String token) {
        try{
            String tokenWithoutBearer = token.replace("Bearer", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(claims.getStringClaim("email"));
            registerRequest.setKeyCloakId(claims.getStringClaim("sub"));
            registerRequest.setPassword("dummy@123123");
            registerRequest.setFirstName(claims.getStringClaim("given_name"));
            registerRequest.setLastName(claims.getStringClaim("family_name"));
            return  registerRequest;

        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    public Mono<UserResponse> registerUser(RegisterRequest request) {
        log.info("Calling User Registration API for email: {}", request.getEmail());

        return userServiceWebClient.post()
                .uri("/api/users/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        return Mono.error(new RuntimeException("Internal server error  " + e.getMessage()));
                    } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        return Mono.error(new RuntimeException("invalid/bad request"));
                    }
                    return Mono.error(new RuntimeException("Unexpected error: " + e.getMessage()));
                });


    }
}
