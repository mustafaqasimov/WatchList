package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.RefreshToken;
import com.movie.watchlist.entity.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
    void deleteByUser(User user);
}
