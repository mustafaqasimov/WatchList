package com.movie.watchlist.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.watchlist.entity.entities.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
        Sən film platforması üçün "Niyə baxmalısan?" bloku yazan köməkçisən.
        Məqsəd: istifadəçiyə bu filmə NƏYƏ GÖRƏ baxmağa dəyər olduğunu göstərmək.
        SÜJETİ DANIŞMA. Hər maddə filmin bir güclü tərəfini izah etməlidir.

        Qaydalar:
        - Dəqiq 4 maddə yaz.
        - Hər maddə 15-25 söz, tam cümlə olsun.
        - Hər maddə fərqli bucaqdan yazılsın: atmosfer/vizual, mövzu və ideya,
          personajlar/tempo, və kimə uyğundur (hansı zövqü olan tamaşaçıya).
        - Yalnız verilən məlumata (təsvir, janr, reytinq) əsaslan.
          Aktyor, rejissor, mükafat və ya süjet detalı UYDURMA.
        - Spoiler vermə, sonluğu açma.
        - Dil: Azərbaycan dili.
        """;

    @SuppressWarnings("unchecked")
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
                        "maxOutputTokens", 2048,
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "ARRAY",
                                "items", Map.of("type", "STRING"))
                )
        );

        Map<String, Object> response = geminiRestClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        log.info("Gemini raw response: {}", response);

        String text = extractText(response);
        return parseBullets(text);
    }

    private List<String> parseBullets(String raw) {
        try {
            List<String> bullets = objectMapper.readValue(raw, new TypeReference<>() {});
            return bullets.stream().limit(4).toList();
        } catch (JsonProcessingException e) {
            log.error("AI response parsing failed: {}", raw, e);
            throw new IllegalStateException("AI response is not in the expected format");
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) return "";
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) return "";
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        if (content == null) return "";
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) return "";
        Object text = parts.get(0).get("text");
        return text == null ? "" : text.toString();
    }
}
