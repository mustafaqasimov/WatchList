package com.movie.watchlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Favorite response DTO")
public class FavoriteResponse {
    @Schema(description = "Favorite ID", example = "1")
    Long id;
    @Schema(description = "Movie ID", example = "12345")
    Long movieId;
    @Schema(description = "TMDB ID", example = "603")
    Long tmdbId;
    @Schema(description = "Movie Name", example = "The Matrix")
    String movieName;
    @Schema(description = "Poster Path", example = "/path/to/poster.jpg")
    String posterPath;
    @Schema(description = "Creation Timestamp", example = "2023-01-01T00:00:00Z")
    LocalDateTime createdAt;
}
