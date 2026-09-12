package com.movie.watchlist.config;

import com.movie.watchlist.dto.records.TmdbMovie;
import com.movie.watchlist.dto.records.TmdbMovieResponse;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("popularMovies", cacheConfig(TmdbMovieResponse.class, Duration.ofHours(6)))
                .withCacheConfiguration("movieSearch", cacheConfig(TmdbMovieResponse.class, Duration.ofMinutes(30)))
                .withCacheConfiguration("movieDetails", cacheConfig(TmdbMovie.class, Duration.ofHours(24)));
    }

    private <T> RedisCacheConfiguration cacheConfig(Class<T> type, Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new JacksonJsonRedisSerializer<>(type)))
                .disableCachingNullValues();
    }
}
