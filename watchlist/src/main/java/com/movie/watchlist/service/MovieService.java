package com.movie.watchlist.service;

import com.movie.watchlist.dto.MovieRequestDTO;
import com.movie.watchlist.dto.MovieResponseDTO;

import java.util.List;

public interface MovieService {

    MovieResponseDTO addMovie(MovieRequestDTO dto);
    MovieResponseDTO getMovieById(Long id);
    List<MovieResponseDTO> getAllMovies();
    void deleteMovie(Long id);

}
