package com.movie.watchlist.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSummaryService {

    private final MovieRepository movieRepository;
    private final MovieAiService aiService;
    private final ObjectMapper objectMapper;

    public List<String> getWhyWatch(Long movieId) {
        Movie movie = movieRepository.findByIdAndActiveStatus(movieId, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        if (movie.getAiSummary() != null) {
            return readBullets(movie.getAiSummary());
        }

        if (movie.getDescription() == null || movie.getDescription().isBlank()) {
            return List.of();
        }

        try {
            List<String> bullets = aiService.generateWhyWatch(movie);
            movie.setAiSummary(objectMapper.writeValueAsString(bullets));
            movie.setAiSummaryGeneratedAt(Instant.now());
            movieRepository.save(movie);
            return bullets;
        } catch (Exception e) {
            log.error("AI xülasəsi generasiya olunmadı, movieId={}", movieId, e);
            return List.of();
        }
    }

    private List<String> readBullets(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
