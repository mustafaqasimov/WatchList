package com.movie.watchlist.dto.records;

import io.swagger.v3.oas.annotations.media.Schema;

public record TmdbGenre(@Schema(description = "The ID of the genre") int id,
                        @Schema(description = "The name of the genre") String name) {}
