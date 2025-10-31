package com.mindmirror.mindmirror_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    // ✅ Correct endpoint for Gemini 2.5 Flash
    private final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final WebClient webClient = WebClient.create();

    public String getAIResponse(String prompt) {
        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    )
            );

            Map<String, Object> response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        return Mono.just(Map.of("error", e.getMessage()));
                    })
                    .block();

            if (response == null) {
                return "⚠ No response received from Gemini.";
            }

            // ✅ Handle standard Gemini response structure
            if (response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
                    if (firstCandidate.containsKey("content")) {
                        Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
                        if (content.containsKey("parts")) {
                            List<?> parts = (List<?>) content.get("parts");
                            if (!parts.isEmpty()) {
                                Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
                                if (firstPart.containsKey("text")) {
                                    return firstPart.get("text").toString();
                                }
                            }
                        }
                    }
                }
            }

            if (response.containsKey("error")) {
                return "❌ Gemini API Error: " + response.get("error");
            }

            return "⚠ Unable to parse Gemini response.";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Exception while contacting Gemini: " + e.getMessage();
        }
    }
}