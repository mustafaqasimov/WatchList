package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.response.FavoriteResponse;
import com.movie.watchlist.entity.entities.Favorite;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.ResourceAlreadyExistsException;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.FavoriteMapper;
import com.movie.watchlist.repositories.FavoriteRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.service.interfaces.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MovieImportService movieImportService;
    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional
    public FavoriteResponse addFavorite(Long userId, Long tmdbId) {
        log.debug("Adding movie with TMDB ID {} to favorites for user ID {}", tmdbId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID: " + userId));

        Movie movie = movieImportService.getOrImportMovie(tmdbId);

        Optional<Favorite> existingFavorite =
                favoriteRepository.findByUserAndMovie(user, movie);

        if (existingFavorite.isPresent()) {
            Favorite favorite = existingFavorite.get();

            if (favorite.getActiveStatus() == ActiveStatus.ACTIVE) {
                log.warn("User ID {} attempted to add duplicate favorite movie TMDB ID {}",
                        userId, tmdbId);

                throw new ResourceAlreadyExistsException("Movie already in favorites");
            }

            favorite.setActiveStatus(ActiveStatus.ACTIVE);
            Favorite restoredFavorite = favoriteRepository.save(favorite);

            log.info("Restored favorite movie TMDB ID {} for user ID {}",
                    tmdbId, userId);

            return favoriteMapper.toDTO(restoredFavorite);
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .movie(movie)
                .activeStatus(ActiveStatus.ACTIVE)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        log.info("Successfully added movie TMDB ID {} to favorites for user ID {}",
                tmdbId, userId);

        return favoriteMapper.toDTO(savedFavorite);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponse> getFavorites(Long userId, Pageable pageable) {
        log.debug("Fetching favorites page for user ID {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        return favoriteRepository.findAllByUserIdAndActiveStatus(userId, ActiveStatus.ACTIVE, pageable)
                .map(favoriteMapper::toDTO);
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long tmdbId) {
        log.debug("Removing movie with TMDB ID {} from favorites for user ID {}", tmdbId, userId);

        Favorite favorite = favoriteRepository.findByUserIdAndMovieTmdbIdAndActiveStatus(userId,
                        tmdbId,
                        ActiveStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Favorite movie not found for user ID {} and TMDB ID {}", userId, tmdbId);
                    return new ResourceNotFoundException("Movie not found in favorites");
                });
        favorite.setActiveStatus(ActiveStatus.INACTIVE);
        favoriteRepository.save(favorite);
        log.info("Successfully removed movie TMDB ID {} from favorites for user ID {}", tmdbId, userId);
    }
}
