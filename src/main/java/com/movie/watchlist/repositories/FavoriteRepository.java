package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.Favorite;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.entity.entities.User;
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


    Page<Favorite> findAllByUserId(
            Long userId,
            Pageable pageable
    );

    Optional<Favorite> findByUserIdAndMovieTmdbId(
            Long userId,
            Long tmdbId
    );
}
