package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.repositories.MovieRepository;
import com.movie.watchlist.service.interfaces.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository repository;
    private final MovieMapper movieMapper;

    @Override
    public MovieResponse addMovie(MovieRequest dto) {
        Movie movie = movieMapper.toEntity(dto);
        Movie saved = repository.save(movie);
        return movieMapper.toDTO(saved);
    }

    @Override
    public MovieResponse getMovieById(Long id) {
        return movieMapper.toDTO(repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Movie not found")));
    }

    @Override
    public List<MovieResponse> getAllMovies() {
        return repository.findAllByActiveStatus(ActiveStatus.ACTIVE)
                .stream().map(movieMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteMovie(Long id) {
        Movie entity = repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        entity.setActiveStatus(ActiveStatus.INACTIVE);
        repository.save(entity);
    }
}
