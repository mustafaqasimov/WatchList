package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.Watchlist;
import com.movie.watchlist.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {
    List<Watchlist> findByUserIdAndActiveStatus(Long userId, ActiveStatus activeStatus);

    Optional<Watchlist> findByIdAndUserIdAndActiveStatus(Long id, Long userId, ActiveStatus activeStatus);

    boolean existsByUserIdAndNameAndActiveStatus(Long userId, String name, ActiveStatus activeStatus);
}
