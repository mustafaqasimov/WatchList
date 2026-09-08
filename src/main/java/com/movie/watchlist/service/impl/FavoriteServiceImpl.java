package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.response.FavoriteResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.mapper.FavoriteMapper;
import com.movie.watchlist.repositories.FavoriteRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.service.interfaces.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MovieImportService movieImportService;
    private final FavoriteMapper favoriteMapper;

    @Override
    public FavoriteResponse addFavorite(Long userId, Long tmdbId) {
        Movie movie = movieImportService.getOrImportMovie(tmdbId);
        return null;
    }

    @Override
    public Page<FavoriteResponse> getFavorites(Long userId, Pageable pageable) {
        return null;
    }

    @Override
    public void removeFavorite(Long userId, Long tmdbId) {

    }
}
