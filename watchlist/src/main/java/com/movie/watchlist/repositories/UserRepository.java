package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByIdAndActiveStatus(Long id, ActiveStatus activeStatus);

    List<User> findAllByActiveStatus(ActiveStatus activeStatus);
}
