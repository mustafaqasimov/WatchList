package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.response.FavoriteResponse;
import com.movie.watchlist.entity.entities.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(target = "movieId", source = "movie.id")
    @Mapping(target = "movieName", source = "movie.movieName")
    @Mapping(target = "posterPath", source = "movie.posterPath")
    @Mapping(target = "tmdbId", source = "movie.tmdbId")
    FavoriteResponse toDTO(Favorite favorite);
}
