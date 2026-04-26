package com.learning.assistant.Service;

import com.learning.assistant.dto.EvaluationRequest;
import com.learning.assistant.dto.LearningRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class LearningService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder().build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ FINAL WORKING URL (UPDATED)
    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";

    // ✅ Learn Concept
    public String getExplanation(LearningRequest request) {
        System.out.println("GEMINI KEY: " + apiKey);

        String prompt = "Explain " + request.getTopic() +
                " in " + request.getLevel() + " level with simple examples.";

        return callGemini(prompt);
    }

    // ✅ Generate Quiz
    public String generateQuiz(LearningRequest request) {

        String prompt = "Generate 3 multiple choice questions on "
                + request.getTopic()
                + " for a " + request.getLevel()
                + " learner. Also provide correct answers.";

        return callGemini(prompt);
    }

    // ✅ Evaluate Answer
    public String evaluateAnswer(EvaluationRequest request) {

        String prompt = "Evaluate the answer:\n" +
                "Question: " + request.getQuestion() +
                "\nUser Answer: " + request.getUserAnswer() +
                "\nCorrect Answer: " + request.getCorrectAnswer() +
                "\nGive feedback and score out of 10.";

        return callGemini(prompt);
    }

    // 🔥 Gemini API Call (FINAL FIXED)
    private String callGemini(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        try {
            String response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("generativelanguage.googleapis.com")
                            .path("/v1/models/gemini-2.5-flash:generateContent") // ✅ FIXED
                            .queryParam("key", apiKey) // ✅ API KEY
                            .build()
                    )
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractGeminiResponse(response);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 helpful for debugging
            return "❌ Gemini Error: " + e.getMessage();
        }
    }

    // ✅ Extract Gemini response
    private String extractGeminiResponse(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            return "⚠️ Unable to parse Gemini response";
        }
    }
}