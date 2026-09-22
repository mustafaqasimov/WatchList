package com.movie.watchlist.dto.records;

public record ReviewUpdateRequest(
        Double rating,
        String content
) {
}
