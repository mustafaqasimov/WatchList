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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieImportService {

    private final TmdbMovieService tmdbMovieService;
    private final TmdbMovieMapper tmdbMapper;
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Transactional
    public int importPopularMovies(int page) {
        Map<Integer, String> genreMap = tmdbMovieService.getGenreMap();
        TmdbMovieResponse response = tmdbMovieService.getPopularMovies(page);

        int imported = 0;
        for (TmdbMovie tmdb : response.results()) {
            if (movieRepository.existsByTmdbId(tmdb.id())) continue;

            MovieRequest request = tmdbMapper.toMovieRequest(tmdb, genreMap);
            request.setPopular(true);
            movieService.addMovies(List.of(request));
            imported++;
        }
        return imported;
    }

    @Transactional
    public Movie getOrImportMovie(long tmdbId) {
        Optional<Movie> existingMovie = movieRepository.findByTmdbId(tmdbId);
        if (existingMovie.isPresent()) {
            Movie movie = existingMovie.get();
            if (movie.getActiveStatus() == ActiveStatus.INACTIVE) {
                movie.setActiveStatus(ActiveStatus.ACTIVE);
                movieRepository.save(movie);
            }
            return movie;
        }
        TmdbMovie details = tmdbMovieService.getMovieDetails(tmdbId);
        MovieRequest request = tmdbMapper.toMovieRequest(details, Map.of());
        request.setPopular(false);
        MovieResponse response = movieService.addMovies(List.of(request))
                .getFirst();
        return movieRepository.findById(response.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found after import"));
    }
}
