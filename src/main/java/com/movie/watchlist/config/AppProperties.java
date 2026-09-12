package com.movie.watchlist.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.app")
public class AppProperties {
    private String frontendUrl;
    private int verificationTokenExpirationMinutes;
    private int resetTokenExpirationMinutes;
}
