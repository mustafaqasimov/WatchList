package com.movie.watchlist.service;

import com.movie.watchlist.dto.MovieRequestDTO;
import com.movie.watchlist.dto.MovieResponseDTO;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.repositories.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository repository;
    private final MovieMapper mapper;

    @Override
    public MovieResponseDTO addMovie(MovieRequestDTO dto) {
        Movie movie = mapper.toEntity(dto);
        Movie saved = repository.save(movie);
        return mapper.toDTO(saved);
    }

    @Override
    public MovieResponseDTO getMovieById(Long id) {
        return mapper.toDTO(repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Movie not found")));
    }

    @Override
    public List<MovieResponseDTO> getAllMovies() {
        return repository.findAllByActiveStatus(ActiveStatus.ACTIVE)
                .stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteMovie(Long id) {
        Movie entity = repository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        entity.setActiveStatus(ActiveStatus.INACTIVE);
        repository.save(entity);
    }
}
