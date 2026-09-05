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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository repository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public List<MovieResponse> addMovies(List<MovieRequest> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }

        List<Movie> movies = movieMapper.toEntityList(dtos);
        List<Movie> savedMovies = repository.saveAll(movies);
        return movieMapper.toDTOList(savedMovies);
    }

    @Override
    public MovieResponse getMovieById(Long id) {
        return movieMapper.toDTO(repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found")));
    }

    @Override
    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return repository.findAllByActiveStatus(ActiveStatus.ACTIVE, pageable)
                .map(movieMapper::toDTO);
    }

    @Override
    public void deleteMovie(Long id) {
        Movie entity = repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        entity.setActiveStatus(ActiveStatus.INACTIVE);
        repository.save(entity);
    }
}
