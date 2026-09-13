package com.movie.watchlist.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "blacklist:";

    public void blacklist(String token, Date expiration) {
        if (expiration == null) {
            log.warn("Cannot blacklist token: expiration date is null");
            return;
        }

        Instant now = Instant.now();
        Instant expiryInstant = expiration.toInstant();
        Duration ttl = Duration.between(now, expiryInstant);

        // Əgər tokenin vaxtı artıq bitibsə, Redis-ə yazmağa ehtiyac yoxdur
        if (ttl.isNegative() || ttl.isZero()) {
            log.debug("Token is already expired. Skipping Redis blacklisting.");
            return;
        }

        try {
            redisTemplate.opsForValue().set(
                    PREFIX + token,
                    "true",
                    ttl
            );
            log.info("Token blacklisted successfully for duration: {} seconds", ttl.getSeconds());
        } catch (Exception ex) {
            log.error("Failed to add token to Redis blacklist: {}", ex.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Boolean hasKey = redisTemplate.hasKey(PREFIX + token);
            return Boolean.TRUE.equals(hasKey);
        } catch (Exception ex) {
            // Redis əlçatan olmadıqda tətbiq çökmür, xəbərdarlıq loglanır
            log.error("Redis connection error during token blacklist check: {}. Allowing request as fallback.", ex.getMessage());
            return false;
        }
    }
}
