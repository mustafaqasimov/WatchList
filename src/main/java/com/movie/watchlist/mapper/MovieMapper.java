package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toEntity(MovieRequest movieRequestDTO);

    MovieResponse toDTO(Movie movie);

    List<Movie> toEntityList(List<MovieRequest> dtos);

    List<MovieResponse> toDTOList(List<Movie> movies);
}
