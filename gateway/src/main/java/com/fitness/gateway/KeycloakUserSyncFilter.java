package com.fitness.gateway;

import com.fitness.gateway.user.RegisterRequest;
import com.fitness.gateway.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain){
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Only fetch register details if we have a token
        RegisterRequest registerRequest = (token != null) ? userService.getUserDetails(token) : null;

        // compute a single user id to use (prefer header, fallback to registerRequest)
        String computedUserId = (userId != null) ? userId : (registerRequest != null ? registerRequest.getKeyCloakId() : null);

        // if we have neither a user id nor a token (and thus no register info), continue the chain
        if (computedUserId == null && token == null) {
            return chain.filter(exchange);
        }

        final String finalUserId = computedUserId; // effectively final for lambdas

        if (finalUserId != null) {
            return userService.validateUser(finalUserId)
                    .flatMap(exists -> {
                        if (!exists) {
                            log.info("User {} does not exist in user-service", finalUserId);

                            if(registerRequest == null) {
                                // nothing to register, return empty to continue chain
                                return Mono.empty();
                            }
                            return userService.registerUser(registerRequest)
                                    .then(Mono.empty());
                        } else {
                            log.info("User {} already exists in user-service", finalUserId);
                            return Mono.empty();
                        }
                    })
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        // Log and continue the chain if user service is unreachable or returns an error
                        log.warn("Error calling user service for userId {}: {}", finalUserId, ex.getMessage());
                        return Mono.empty();
                    })
                    .then(Mono.defer(() -> {
                        // Mutate request to add header and continue the chain
                        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", finalUserId)
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }));
        }

        // If no finalUserId but token exists (we couldn't compute id), still continue the chain
        return chain.filter(exchange);
    }

}
