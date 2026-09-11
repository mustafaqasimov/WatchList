package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.records.TmdbMovie;
import com.movie.watchlist.dto.records.TmdbMovieResponse;
import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.mapper.TmdbMovieMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.service.interfaces.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieImportService {

    private final TmdbMovieService tmdbMovieService;
    private final TmdbMovieMapper tmdbMapper;
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    public int importPopularMovies(int page) {
        log.info("Starting popular movies import from TMDB for page: {}", page);

        Map<Integer, String> genreMap = tmdbMovieService.getGenreMap();
        TmdbMovieResponse response = tmdbMovieService.getPopularMovies(page);

        if (response == null || response.results() == null || response.results().isEmpty()) {
            log.warn("No movies returned from TMDB API for page: {}", page);
            return 0;
        }

        List<TmdbMovie> tmdbMovies = response.results();

        List<Long> incomingTmdbIds = tmdbMovies.stream().map(TmdbMovie::id).toList();
        Set<Long> existingTmdbIds = movieRepository.findAllTmdbIdsIn(incomingTmdbIds);

        List<MovieRequest> requestsToImport = new ArrayList<>();
        for (TmdbMovie tmdb : tmdbMovies) {
            if (!existingTmdbIds.contains(tmdb.id())) {
                MovieRequest request = tmdbMapper.toMovieRequest(tmdb, genreMap);
                request.setPopular(true);
                requestsToImport.add(request);
            }
        }

        if (requestsToImport.isEmpty()) {
            log.info("All movies from TMDB page {} already exist in the database", page);
            return 0;
        }

        List<MovieResponse> savedMovies = movieService.addMovies(requestsToImport);
        log.info("Successfully imported {} new popular movies from TMDB page {}", savedMovies.size(), page);

        return savedMovies.size();
    }

    @Transactional
    public Movie getOrImportMovie(long tmdbId) {
        log.debug("Fetching or importing movie with TMDB ID: {}", tmdbId);

        return movieRepository.findByTmdbId(tmdbId)
                .map(movie -> {
                    if (movie.getActiveStatus() == ActiveStatus.INACTIVE) {
                        log.info("Reactivating INACTIVE movie with TMDB ID: {}", tmdbId);
                        movie.setActiveStatus(ActiveStatus.ACTIVE);
                        // Dirty Checking avtomatik UPDATE edəcək
                    }
                    return movie;
                })
                .orElseGet(() -> importSingleMovieFromTmdb(tmdbId));
    }

    private Movie importSingleMovieFromTmdb(long tmdbId) {
        log.info("Movie with TMDB ID {} not found in DB. Importing from TMDB API...", tmdbId);

        TmdbMovie details = tmdbMovieService.getMovieDetails(tmdbId);
        if (details == null) {
            log.error("Failed to fetch movie details from TMDB API for ID: {}", tmdbId);
            throw new ResourceNotFoundException("Movie not found on TMDB with ID: " + tmdbId);
        }

        MovieRequest request = tmdbMapper.toMovieRequest(details, Map.of());
        request.setPopular(false);

        Movie movieEntity = movieMapper.toEntity(request);
        movieEntity.setActiveStatus(ActiveStatus.ACTIVE);

        Movie savedMovie = movieRepository.save(movieEntity);
        log.info("Successfully imported and saved single movie '{}' (ID: {})", savedMovie.getMovieName(), savedMovie.getId());

        return savedMovie;
    }
}
