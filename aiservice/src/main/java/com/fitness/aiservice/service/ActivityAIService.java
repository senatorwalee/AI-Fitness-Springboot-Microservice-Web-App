package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAIService {
    private  final GeminiService geminiService;

    public String generateRecommendation(Activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("RESPONSE FROM AI: {} ", aiResponse);
        return aiResponse;
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                Analyze this fitness activity and provide detailed recommendations in the following exact JSON format:
                
                {
                "overallAnalysis": {
                "feedback": "Overall feedback on the activity.",
                "summary": "Brief summary of the activity's performance."
                },
                "paceAnalysis": {
                "feedback": "Feedback on the pace, e.g., 'consistent', 'varied', 'too fast/slow'.",
                "recommendation": "Recommendations for pace improvement or maintenance."
                },
                "heartRateAnalysis": {
                "feedback": "Feedback on heart rate zones and consistency.",
                "recommendation": "Recommendations for heart rate management during activity."
                },
                "caloriesBurnedAnalysis": {
                "feedback": "Feedback on calories burned relative to activity type and duration.",
                "recommendation": "Recommendations related to calorie expenditure and energy balance."
                },
                "improvements": [
                {
                "area": "Specific area for improvement, e.g., 'Stamina', 'Strength', 'Form'.",
                "recommendation": "Detailed recommendation for improvement in this area."
                }
                ],
                "workoutSuggestions": [
                {
                "workout": "Name of suggested workout, e.g., 'Interval Training', 'Long Run', 'Strength Training'.",
                "description": "Brief description of the suggested workout and its benefits."
                }
                ],
                "safetyInstructions": [
                "Specific safety tip 1",
                "Specific safety tip 2"
                ]
                }
                
                The analysis should focus on performance improvements, next workout suggestions, and safety guidelines. Ensure the response follows the EXACT JSON format shown above.
                
                Activity Details:""",
                "Activity Type",  activity.getType(),
                "Duration", activity.getDuration(),
                "Calories Burned",  activity.getCaloriesBurned(),
                "Start Time", activity.getStartTime(),
                "Additional Metrics", activity.getAdditionalMetrics()
        );
    }
}
