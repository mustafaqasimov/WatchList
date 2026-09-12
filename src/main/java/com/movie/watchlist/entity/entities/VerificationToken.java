package com.movie.watchlist.entity.entities;

import com.movie.watchlist.entity.base.BaseEntity;
import com.movie.watchlist.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "verification_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
public class VerificationToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    TokenType type;

    Instant expiryDate;

    boolean used;
}