package com.movie.watchlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Favorite request DTO")
public class FavoriteRequest {
    @Schema(description = "TMDB ID of the movie", example = "12345")
    Long tmdbId;
}
