package com.movie.watchlist.watchlist.service;

import com.movie.watchlist.service.impl.TokenBlacklistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    private static final String TOKEN = "sample.jwt.token";

    // ---------- blacklist ----------

    @Test
    void blacklist_doesNothing_whenExpirationIsNull() {
        tokenBlacklistService.blacklist(TOKEN, null);

        verifyNoInteractions(redisTemplate);
    }

    @Test
    void blacklist_doesNothing_whenTokenAlreadyExpired() {
        Date pastExpiration = Date.from(Instant.now().minusSeconds(60));

        tokenBlacklistService.blacklist(TOKEN, pastExpiration);

        verifyNoInteractions(redisTemplate);
    }

    @Test
    void blacklist_storesTokenInRedisWithCorrectTtl_whenExpirationIsInFuture() {
        Date futureExpiration = Date.from(Instant.now().plusSeconds(900));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        tokenBlacklistService.blacklist(TOKEN, futureExpiration);

        verify(valueOperations).set(
                eq("blacklist:" + TOKEN),
                eq("true"),
                argThat((Duration ttl) -> ttl.getSeconds() > 895 && ttl.getSeconds() <= 900)
        );
    }

    @Test
    void blacklist_doesNotThrow_whenRedisOperationFails() {
        Date futureExpiration = Date.from(Instant.now().plusSeconds(900));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doThrow(new RuntimeException("Redis unavailable"))
                .when(valueOperations).set(anyString(), anyString(), any(Duration.class));

        // should not propagate the exception - failure is logged and swallowed
        tokenBlacklistService.blacklist(TOKEN, futureExpiration);

        verify(valueOperations).set(anyString(), anyString(), any(Duration.class));
    }

    // ---------- isBlacklisted ----------

    @Test
    void isBlacklisted_returnsFalse_whenTokenIsNull() {
        boolean result = tokenBlacklistService.isBlacklisted(null);

        assertThat(result).isFalse();
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void isBlacklisted_returnsFalse_whenTokenIsBlank() {
        boolean result = tokenBlacklistService.isBlacklisted("   ");

        assertThat(result).isFalse();
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void isBlacklisted_returnsTrue_whenKeyExistsInRedis() {
        when(redisTemplate.hasKey("blacklist:" + TOKEN)).thenReturn(true);

        boolean result = tokenBlacklistService.isBlacklisted(TOKEN);

        assertThat(result).isTrue();
    }

    @Test
    void isBlacklisted_returnsFalse_whenKeyDoesNotExistInRedis() {
        when(redisTemplate.hasKey("blacklist:" + TOKEN)).thenReturn(false);

        boolean result = tokenBlacklistService.isBlacklisted(TOKEN);

        assertThat(result).isFalse();
    }

    @Test
    void isBlacklisted_returnsFalse_whenRedisHasKeyReturnsNull() {
        when(redisTemplate.hasKey("blacklist:" + TOKEN)).thenReturn(null);

        boolean result = tokenBlacklistService.isBlacklisted(TOKEN);

        assertThat(result).isFalse();
    }

    @Test
    void isBlacklisted_returnsFalseAsFallback_whenRedisThrowsException() {
        when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Connection refused"));

        boolean result = tokenBlacklistService.isBlacklisted(TOKEN);

        assertThat(result).isFalse();
    }
}
