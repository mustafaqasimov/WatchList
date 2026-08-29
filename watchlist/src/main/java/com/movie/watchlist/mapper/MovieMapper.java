package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.MovieRequestDTO;
import com.movie.watchlist.dto.MovieResponseDTO;
import com.movie.watchlist.entity.entities.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public Movie toEntity(MovieRequestDTO movieRequestDTO) {
        if (movieRequestDTO == null) return null;

        Movie movie = new Movie();
        movie.setDescription(movieRequestDTO.getDescription());
        movie.setReleaseDate(movieRequestDTO.getReleaseDate());
        movie.setRating(movieRequestDTO.getRating());

        movie.setGenre(movieRequestDTO.getGenre());
        return movie;
    }

    public MovieResponseDTO toDTO(Movie movie) {
        if (movie == null) return null;

        MovieResponseDTO movieResponseDTO = new MovieResponseDTO();

        movieResponseDTO.setId(movie.getId());
        movieResponseDTO.setDescription(movie.getDescription());
        movieResponseDTO.setReleaseDate(movie.getReleaseDate());
        movieResponseDTO.setRating(movie.getRating());

        movieResponseDTO.setGenre(movie.getGenre());
        return movieResponseDTO;
    }
}
