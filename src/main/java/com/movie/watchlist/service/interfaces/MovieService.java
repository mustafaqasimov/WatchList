package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MovieService {

    List<MovieResponse> addMovies(List<MovieRequest> dtos);
    MovieResponse getMovieById(Long id);
    Page<MovieResponse> getPopularMovies(Pageable pageable);
    void deleteMovie(Long id);
    Object getMovieVideos(Long tmdbId);

}
