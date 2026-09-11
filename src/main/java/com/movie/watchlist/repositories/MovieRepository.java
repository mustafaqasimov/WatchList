package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByIdAndActiveStatus(Long id, ActiveStatus activeStatus);
    Page<Movie> findAllByActiveStatus(ActiveStatus activeStatus, Pageable pageable);

    Optional<Movie> findByTmdbId(Long tmdbId);

    Page<Movie> findAllByPopularTrueAndActiveStatus(ActiveStatus activeStatus, Pageable pageable);

    @Query("SELECT m.tmdbId FROM Movie m WHERE m.tmdbId IN :tmdbIds")
    Set<Long> findAllTmdbIdsIn(@Param("tmdbIds") List<Long> tmdbIds);
}
