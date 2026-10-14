package com.movie.watchlist.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.watchlist.entity.entities.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieAiService {

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.model}")
    private String model;

    private static final String SYSTEM_PROMPT = """
            Sən film platforması üçün qısa və cəlbedici xülasələr yazan köməkçisən.
            Qaydalar:
            - Yalnız verilən məlumata əsaslan, özündən fakt, aktyor adı və ya süjet detalı uydurma.
            - Spoiler vermə, sonluğu açma.
            - Dəqiq 3 maddə yaz, hər biri maksimum 12 söz.
            - Dil: Azərbaycan dili.
            """;

    public List<String> generateWhyWatch(Movie movie) {
        String userPrompt = """
                Film: %s
                Janr: %s
                Reytinq: %s/10
                Təsvir: %s
                """.formatted(movie.getMovieName(), movie.getGenre(),
                movie.getRating(), movie.getDescription());

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", SYSTEM_PROMPT))),
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", userPrompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.4,
                        "maxOutputTokens", 1024,
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "ARRAY",
                                "items", Map.of("type", "STRING")),
                        "thinkingConfig", Map.of("thinkingBudget", 0)
                )
        );

        JsonNode response = geminiRestClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        String text = response.path("candidates").path(0)
                .path("content").path("parts").path(0).path("text").asText();

        return parseBullets(text);
    }

    private List<String> parseBullets(String raw) {
        try {
            List<String> bullets = objectMapper.readValue(raw, new TypeReference<>() {});
            return bullets.stream().limit(3).toList();
        } catch (JsonProcessingException e) {
            log.error("AI response parsing failed: {}", raw, e);
            throw new IllegalStateException("AI response is not in the expected format");
        }
    }
}
