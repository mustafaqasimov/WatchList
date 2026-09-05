package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.records.TmdbGenre;
import com.movie.watchlist.dto.records.TmdbGenreListResponse;
import com.movie.watchlist.dto.records.TmdbMovie;
import com.movie.watchlist.dto.records.TmdbMovieResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TmdbMovieService {

    private final RestClient tmdbRestClient;

    public TmdbMovieResponse getPopularMovies(int page) {
        try {
            return get("/movie/popular", Map.of("page", String.valueOf(page)));
        } catch (HttpClientErrorException.Forbidden e) {
            System.err.println(">>> TMDB 403 Response Body: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    public TmdbMovieResponse searchMovies(String query, int page) {
        return get("/search/movie", Map.of("query", query, "page", String.valueOf(page)));
    }

    public TmdbMovie getMovieDetails(long tmdbId) {
        return tmdbRestClient.get()
                .uri("/movie/{id}", tmdbId)
                .retrieve()
                .body(TmdbMovie.class);
    }

    public Map<Integer, String> getGenreMap() {
        TmdbGenreListResponse response = tmdbRestClient.get()
                .uri("/genre/movie/list")
                .retrieve()
                .body(TmdbGenreListResponse.class);

        if (response == null || response.genres() == null) {
            return Map.of();
        }

        return response.genres().stream()
                .collect(Collectors.toMap(TmdbGenre::id, TmdbGenre::name));
    }

    private TmdbMovieResponse get(String path, Map<String, String> params) {
        return tmdbRestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    params.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .retrieve()
                .body(TmdbMovieResponse.class);
    }
}
