package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Service for generating AI-based recommendations for fitness activities using Gemini AI.
 * Handles the creation of prompts, API calls, and processing of AI responses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAIService {
    /**
     * Service for interacting with the Gemini AI API.
     */
    private final GeminiService geminiService;

    /**
     * Generates AI-based recommendations for a given activity.
     * Sends the activity details to Gemini AI for analysis and returns the response.
     *
     * @param activity the activity to generate recommendations for
     * @return the AI-generated recommendation response as a String
     */
    public Recommendation generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        if (aiResponse != null && aiResponse.contains("\"error\"")) {
            log.warn("API returned an error response: {}", aiResponse);

        }

        log.info("RESPONSE FROM AI: {} ", aiResponse);
        return processAiResponse(activity,aiResponse);

    }

    /**
     * Processes the AI response by extracting and parsing the JSON content.
     * Maps the parsed data to the Recommendation entity.
     *
     * @param activity the activity that was analyzed
     * @param aiResponse the raw AI response from Gemini API
     */
    private Recommendation processAiResponse(Activity activity, String aiResponse){
        Recommendation recommendation = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text");
            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n", "")
                    .replaceAll("\\n```", "")
                    .trim();
            log.info("PARSED RESPONSE FROM AI: {} ", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);

            // Build the overall analysis string
            String overallAnalysis = buildOverallAnalysisString(analysisJson);

            // Extract improvements list
            List<String> improvements = extractImprovements(analysisJson);

            // Extract suggestions list
            List<String> suggestions = extractSuggestions(analysisJson);

            // Extract safety instructions list
            List<String> safety = extractSafety(analysisJson);

            // Create Recommendation entity
            recommendation = Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType().toString())
                    .recommendation(overallAnalysis)
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .build();

            log.info("Recommendation created: {}", recommendation);
            return recommendation;


        } catch (Exception e) {
            log.error("Error processing AI response: {}", e.getMessage(), e);
        }
        return createDefaultRecommendation(activity);

    }

    /**
     * Default response if gemini is off line
     */

    private Recommendation createDefaultRecommendation(Activity activity){
        return  Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(String.valueOf(activity.getType()))
                .recommendation("Unable to generate detailed analysis at the moment please try again later")
                .improvements(Collections.singletonList("Continue with your current workout"))
                .suggestions(Collections.singletonList("Consider consulting a Fitness expert if Urgent"))
                .safety(Arrays.asList(
                        "Always warm up before exercise",
                        "Stay hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();

    }

    /**
     * Builds the overall analysis string from the AI response JSON.
     * Combines feedback and summary from the overallAnalysis section.
     *
     * @param analysisJson the parsed AI response
     * @return formatted overall analysis string
     */
    private String buildOverallAnalysisString(JsonNode analysisJson) {
        JsonNode overallNode = analysisJson.path("overallAnalysis");
        if(!overallNode.isMissingNode()){
            String feedback = overallNode.path("feedback").asText();
            String summary = overallNode.path("summary").asText();
            return "Feedback: " + feedback + "\nSummary: " + summary;
        }
        return "";
    }

    /**
     * Extracts improvements from the AI response.
     * Combines area and recommendation for each improvement.
     *
     * @param analysisJson the parsed AI response
     * @return list of improvement recommendations
     */
    private List<String> extractImprovements(JsonNode analysisJson) {
        List<String> improvements = new ArrayList<>();
        JsonNode improvementsNode = analysisJson.path("improvements");

        if (improvementsNode.isArray()) {
            improvementsNode.forEach(item -> {
                String area = item.path("area").asText();
                String recommendation = item.path("recommendation").asText();
                improvements.add(area + ": " + recommendation);
            });
        }

        return improvements;
    }

    /**
     * Extracts workout suggestions from the AI response.
     * Combines workout name and description for each suggestion.
     *
     * @param analysisJson the parsed AI response
     * @return list of workout suggestions
     */
    private List<String> extractSuggestions(JsonNode analysisJson) {
        List<String> suggestions = new ArrayList<>();
        JsonNode suggestionsNode = analysisJson.path("workoutSuggestions");

        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(item -> {
                String workout = item.path("workout").asText();
                String description = item.path("description").asText();
                suggestions.add(workout + ": " + description);
            });
        }

        return suggestions;
    }

    /**
     * Extracts safety instructions from the AI response.
     * Returns a list of individual safety tips.
     *
     * @param analysisJson the parsed AI response
     * @return list of safety instructions
     */
    private List<String> extractSafety(JsonNode analysisJson) {
        List<String> safety = new ArrayList<>();
        JsonNode safetyNode = analysisJson.path("safetyInstructions");

        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }

        return safety;
    }

    /**
     * Creates a detailed prompt string for Gemini AI based on activity details.
     * The prompt instructs the AI to analyze the activity and provide recommendations in JSON format.
     *
     * @param activity the activity to create a prompt for
     * @return the formatted prompt string to send to Gemini AI
     */
    private String createPromptForActivity(Activity activity) {

        String activityJson = String.format("""
    {
      "type": "%s",
      "durationMin": %d,
      "caloriesBurned": %d,
      "startTime": "%s",
      "additionalMetrics": %s
    }
    """,
                safe(String.valueOf(activity.getType())),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                safe(String.valueOf(activity.getStartTime())),
                String.valueOf(activity.getAdditionalMetrics()) // ideally JSON string
        );

        return String.format("""
SYSTEM:
You are a fitness coach assistant. Produce safe, practical recommendations.

OUTPUT RULES (must follow exactly):
- Return ONLY one valid JSON object.
- Do NOT include markdown, backticks, code fences, or any extra text before/after the JSON.
- Use EXACTLY the keys shown in the schema below. Do NOT add extra keys.
- All values must be strings/arrays/objects as specified.
- If a field is unknown, use an empty string "" or an empty array [] (do not invent numbers).

JSON SCHEMA (must match exactly):
{
  "overallAnalysis": {
    "feedback": "string",
    "summary": "string"
  },
  "paceAnalysis": {
    "feedback": "string",
    "recommendation": "string"
  },
  "heartRateAnalysis": {
    "feedback": "string",
    "recommendation": "string"
  },
  "caloriesBurnedAnalysis": {
    "feedback": "string",
    "recommendation": "string"
  },
  "improvements": [
    {
      "area": "string",
      "recommendation": "string"
    }
  ],
  "workoutSuggestions": [
    {
      "workout": "string",
      "description": "string"
    }
  ],
  "safetyInstructions": [
    "string"
  ]
}

CONSTRAINTS:
- Keep recommendations age-appropriate and non-medical.
- Do not diagnose conditions.
- Include at least 2 safetyInstructions.
- improvements: 2 items.
- workoutSuggestions: 2 items.

INPUT ACTIVITY (JSON):
%s
""", activityJson);
    }

    /** small helper to avoid breaking JSON quotes */
    private String safe(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
