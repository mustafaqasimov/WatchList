package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.records.TmdbGenre;
import com.movie.watchlist.dto.records.TmdbGenreListResponse;
import com.movie.watchlist.dto.records.TmdbMovie;
import com.movie.watchlist.dto.records.TmdbMovieResponse;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbMovieService {

    private final RestClient tmdbRestClient;

    public TmdbMovieResponse getPopularMovies(int page) {
        log.debug("Fetching popular movies from TMDB for page: {}", page);
        return executeGet("/movie/popular", Map.of("page", String.valueOf(page)));
    }

    public TmdbMovieResponse searchMovies(String query, int page) {
        log.debug("Searching movies on TMDB with query: '{}', page: {}", query, page);
        return executeGet("/search/movie", Map.of("query", query, "page", String.valueOf(page)));
    }

    public TmdbMovie getMovieDetails(long tmdbId) {
        log.debug("Fetching details from TMDB for movie ID: {}", tmdbId);
        try {
            TmdbMovie movie = tmdbRestClient.get()
                    .uri("/movie/{id}", tmdbId)
                    .retrieve()
                    .body(TmdbMovie.class);

            if (movie == null) {
                log.warn("TMDB API returned null body for movie ID: {}", tmdbId);
                throw new ResourceNotFoundException("Movie details not found on TMDB for ID: " + tmdbId);
            }

            return movie;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Movie with TMDB ID {} not found (404)", tmdbId);
            throw new ResourceNotFoundException("Movie not found on TMDB with ID: " + tmdbId);
        } catch (RestClientException e) {
            log.error("Error communicating with TMDB API for movie ID {}: {}", tmdbId, e.getMessage());
            throw e;
        }
    }

    /**
     * Janrlar nadir hallarda dəyişdiyi üçün nəticəni 'tmdb-genres' cache-ində saxlayırıq.
     */
    @Cacheable(value = "tmdb-genres", unless = "#result == null || #result.isEmpty()")
    public Map<Integer, String> getGenreMap() {
        log.info("Fetching genre list from TMDB API (Cache miss)...");
        try {
            TmdbGenreListResponse response = tmdbRestClient.get()
                    .uri("/genre/movie/list")
                    .retrieve()
                    .body(TmdbGenreListResponse.class);

            if (response == null || response.genres() == null) {
                log.warn("TMDB Genre list response was empty");
                return Map.of();
            }

            return response.genres().stream()
                    .collect(Collectors.toMap(TmdbGenre::id, TmdbGenre::name, (existing, replacement) -> existing));
        } catch (RestClientException e) {
            log.error("Failed to fetch genres from TMDB API: {}", e.getMessage());
            return Map.of();
        }
    }

    private TmdbMovieResponse executeGet(String path, Map<String, String> params) {
        try {
            return tmdbRestClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path(path);
                        params.forEach(uriBuilder::queryParam);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(TmdbMovieResponse.class);
        } catch (HttpClientErrorException.Forbidden e) {
            log.error("TMDB API 403 Forbidden Error for path '{}'. Response body: {}", path, e.getResponseBodyAsString());
            throw e;
        } catch (RestClientException e) {
            log.error("TMDB API request failed for path '{}': {}", path, e.getMessage());
            throw e;
        }
    }
}
