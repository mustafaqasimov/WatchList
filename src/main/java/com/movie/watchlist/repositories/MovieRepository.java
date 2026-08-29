package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByIdAndActiveStatus(Long id, ActiveStatus activeStatus);
    List<Movie> findAllByActiveStatus(ActiveStatus activeStatus);
}
