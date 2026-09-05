package com.movie.watchlist.dto.records;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record TmdbMovie(
        @Schema(description = "The ID of the movie") long id,
        @Schema(description = "The title of the movie") String title,
        @Schema(description = "The overview of the movie") String overview,
        @Schema(description = "The release date of the movie") String release_date,
        @Schema(description = "The path to the movie poster") String poster_path,
        @Schema(description = "The average vote of the movie") double vote_average,
        @Schema(description = "The list of genre IDs") List<Integer> genre_ids,
        @Schema(description = "The list of genres") List<TmdbGenre> genres
) {}
