package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.records.WatchlistUpdateRequest;
import com.movie.watchlist.dto.request.WatchlistItemRequest;
import com.movie.watchlist.dto.request.WatchlistRequest;
import com.movie.watchlist.dto.response.WatchlistItemResponse;
import com.movie.watchlist.dto.response.WatchlistResponse;
import com.movie.watchlist.entity.entities.Watchlist;
import com.movie.watchlist.entity.entities.WatchlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WatchlistMapper {

    @Mapping(target = "itemCount", expression = "java(watchlist.getItems() != null ? watchlist.getItems().size() : 0)")
    WatchlistResponse toWatchlistResponse(Watchlist watchlist);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Watchlist toEntity(WatchlistRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "watchlist", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    WatchlistItem toEntity(WatchlistItemRequest request);

    @Mapping(source = "movie.id", target = "movieId")
    @Mapping(source = "movie.movieName", target = "movieTitle")
    @Mapping(source = "movie.posterPath", target = "moviePosterPath")
    @Mapping(source = "createdAt", target = "addedAt")
    WatchlistItemResponse toItemResponse(WatchlistItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "watchlist", ignore = true)
    @Mapping(target = "movie", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateItemFromRequest(WatchlistUpdateRequest request, @MappingTarget WatchlistItem item);
}
