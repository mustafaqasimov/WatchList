package com.movie.watchlist.dto.records;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TmdbMovieResponse(
        @Schema(description = "The list of movies") List<TmdbMovie> results,
        @Schema(description = "The current page") int page,
        @Schema(description = "The total number of pages") int total_pages,
        @Schema(description = "The total number of results") int total_results
) {}
