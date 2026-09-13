package com.movie.watchlist.repositories;

import com.movie.watchlist.entity.entities.VerificationToken;
import com.movie.watchlist.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByTokenHashAndType(String tokenHash, TokenType type);


}
