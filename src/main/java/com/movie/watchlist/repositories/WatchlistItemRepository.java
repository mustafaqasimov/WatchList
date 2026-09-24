package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.WatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {
    Page<WatchlistItem> findByWatchlistIdAndActiveStatus(Long watchlistId, com.movie.watchlist.enums.ActiveStatus activeStatus, Pageable pageable);

    Optional<WatchlistItem> findByWatchlistIdAndMovieIdAndActiveStatus(Long watchlistId, Long movieId, com.movie.watchlist.enums.ActiveStatus activeStatus);

    boolean existsByWatchlistIdAndMovieIdAndActiveStatus(Long watchlistId, Long movieId, com.movie.watchlist.enums.ActiveStatus activeStatus);
}
