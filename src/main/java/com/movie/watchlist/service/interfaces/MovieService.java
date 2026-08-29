package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;

import java.util.List;

public interface MovieService {

    MovieResponse addMovie(MovieRequest dto);
    MovieResponse getMovieById(Long id);
    List<MovieResponse> getAllMovies();
    void deleteMovie(Long id);

}
