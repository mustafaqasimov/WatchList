package com.movie.watchlist.dto.response;

import com.movie.watchlist.enums.Priority;
import com.movie.watchlist.enums.WatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Watchlist item response DTO")
public class WatchlistItemResponse {
    @Schema(description = "Watchlist item ID",example = "1")
    Long id;
    @Schema(description = "Movie ID",example = "123")
    Long movieId;
    @Schema(description = "Movie title",example = "The Matrix")
    String movieTitle;
    @Schema(description = "Movie poster path",example = "/posters/the-matrix.jpg")
    String moviePosterPath;

    @Schema(description = "Watch status")
    WatchStatus watchStatus;
    @Schema(description = "Priority")
    Priority priority;

    @Schema(description = "Date and time when the item was added to the watchlist")
    LocalDateTime addedAt;
}
