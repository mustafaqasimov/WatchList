package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.Reviews;
import com.movie.watchlist.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Reviews, Long> {

    Optional<Reviews> findByUserIdAndMovieIdAndActiveStatus(Long userId, Long movieId, ActiveStatus active);

    Page<Reviews> findByMovieIdAndActiveStatus(Long movieId, ActiveStatus active, Pageable pageable);
}
