package com.fitness.aiservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * Service for interacting with the Gemini AI API.
 * Handles sending questions and receiving answers from the Gemini endpoint.
 */
@Service
public class GeminiService {
    /**
     * Logger instance for GeminiService operations.
     */
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    /**
     * WebClient instance for making HTTP requests to the Gemini API.
     */
    private final WebClient webClient;

    /**
     * Base URL for the Gemini API, injected from application properties.
     */
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    /**
     * API key for authenticating with the Gemini API, injected from application properties.
     */
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    /**
     * Constructs the GeminiService with a WebClient builder.
     * @param webClientBuilder the WebClient builder for HTTP requests
     */
    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Sends a question to the Gemini API and retrieves the answer as a String.
     * Uses the Gemini 1.5 Flash model for fast text generation.
     * @param question the question to send to Gemini
     * @return the answer from Gemini as a String
     */
    public String getAnswer(String question) {
        try {
            // Build request body in the correct Gemini API v1beta format
            // API format: POST /v1beta/models/{model}:generateContent?key={API_KEY}
            // Request body: {"contents": [{"parts": [{"text": "question"}]}]}
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", question)
                                    )
                            )
                    )
            );

            logger.info("Sending request to Gemini API with question: {}", question);
            logger.debug("API URL: {}", geminiApiUrl);

            // Constructing the full URI with API key
            // helper for if geminiApiUrl already contains ":generateContent?key=", we append only the key
            String fullUri = geminiApiUrl.endsWith("key=") ?
                    geminiApiUrl + geminiApiKey :
                    geminiApiUrl + "?key=" + geminiApiKey;

            logger.debug("Full URI: {}", fullUri);

            String response = webClient.post()
                    .uri(fullUri)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        logger.error("API Error Response: {}", body);
                                        return reactor.core.publisher.Mono.error(
                                                new RuntimeException("API Error: " + body)
                                        );
                                    })
                    )
                    .bodyToMono(String.class)
                    .block();

            logger.info("Received response from Gemini API");
            return response;
        } catch (Exception e) {
            // Log error and return a safe error message to prevent message listener from crashing
            logger.error("Error calling Gemini API: {}", e.getMessage(), e);
            return "Error generating recommendation: " + e.getMessage();
        }
    }
}



