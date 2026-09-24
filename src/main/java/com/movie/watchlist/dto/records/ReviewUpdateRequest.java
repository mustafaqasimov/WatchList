package com.movie.watchlist.dto.records;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewUpdateRequest(
        @Schema(description = "The rating for the review", example = "4.5")
        Double rating,
        @Schema(description = "The content of the review", example = "Great movie!")
        String content
) {
}
