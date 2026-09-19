package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.Favorite;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {


    boolean existsByUserAndMovie(
            User user,
            Movie movie
    );


    Page<Favorite> findAllByUserIdAndActiveStatus(
            Long userId,
            ActiveStatus status,
            Pageable pageable
    );

    Optional<Favorite> findByUserIdAndMovieTmdbIdAndActiveStatus(Long userId, Long tmdbId, ActiveStatus status);
}
