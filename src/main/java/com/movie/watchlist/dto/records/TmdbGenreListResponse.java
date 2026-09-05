package com.movie.watchlist.dto.records;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TmdbGenreListResponse(
        @Schema(description = "The list of genres") List<TmdbGenre> genres) {}
