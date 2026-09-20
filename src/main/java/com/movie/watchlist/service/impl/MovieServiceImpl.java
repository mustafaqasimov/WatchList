package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.service.interfaces.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final MovieRepository repository;
    private final MovieMapper movieMapper;
    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public List<MovieResponse> addMovies(List<MovieRequest> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            log.warn("Empty or null DTO list passed to addMovies");
            return List.of();
        }

        log.debug("Batch adding {} movies to database", dtos.size());
        List<Movie> movies = movieMapper.toEntityList(dtos);

        movies.forEach(movie -> {
            if (movie.getActiveStatus() == null) {
                movie.setActiveStatus(ActiveStatus.ACTIVE);
            }
        });

        List<Movie> savedMovies = repository.saveAll(movies);
        log.info("Successfully added {} movies to catalog", savedMovies.size());

        return movieMapper.toDTOList(savedMovies);
    }

    @Override
    @Transactional(readOnly = true)
    public MovieResponse getMovieById(Long id) {
        log.debug("Fetching active movie with ID: {}", id);

        return repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .map(movieMapper::toDTO)
                .orElseThrow(() -> {
                    log.warn("Active movie not found with ID: {}", id);
                    return new ResourceNotFoundException("Movie not found with ID: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovieResponse> getPopularMovies(Pageable pageable) {
        log.debug("Fetching popular active movies page: {}", pageable.getPageNumber());

        return repository.findAllByPopularTrueAndActiveStatus(ActiveStatus.ACTIVE, pageable)
                .map(movieMapper::toDTO);
    }

    @Override
    @Transactional
    public void deleteMovie(Long id) {
        log.debug("Soft deleting movie with ID: {}", id);

        Movie entity = repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Cannot delete. Active movie not found with ID: {}", id);
                    return new ResourceNotFoundException("Movie not found with ID: " + id);
                });

        entity.setActiveStatus(ActiveStatus.INACTIVE);

        log.info("Successfully soft deleted movie with ID: {}", id);
    }

    @Override
    public Object getMovieVideos(Long tmdbId) {
        String url = "https://api.themoviedb.org/3/movie/" + tmdbId + "/videos?api_key=" + tmdbApiKey;

        return restTemplate.getForObject(url, Object.class);
    }
}
