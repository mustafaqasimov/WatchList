package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.WatchlistItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WatchlistItemRepository extends JpaRepository<WatchlistItem, Long> {
    Page<WatchlistItem> findByWatchlistId(Long watchlistId, Pageable pageable);

    Optional<WatchlistItem> findByWatchlistIdAndMovieId(Long watchlistId, Long movieId);

    boolean existsByWatchlistIdAndMovieId(Long watchlistId, Long movieId);
}
