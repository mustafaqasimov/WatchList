package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{

}
