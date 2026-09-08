package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.response.FavoriteResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {


    FavoriteResponse addFavorite(
            Long userId,
            Long tmdbId
    );


    Page<FavoriteResponse> getFavorites(
            Long userId,
            Pageable pageable
    );


    void removeFavorite(
            Long userId,
            Long tmdbId
    );
}
