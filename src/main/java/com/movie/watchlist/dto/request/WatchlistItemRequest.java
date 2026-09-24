package com.movie.watchlist.dto.request;

import com.movie.watchlist.enums.Priority;
import com.movie.watchlist.enums.WatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Watchlist item request DTO")
public class WatchlistItemRequest {

    @NotNull(message = "Movie ID is required")
    @Schema(description = "ID of the movie to be added to the watchlist", example = "12345")
    Long movieId;

    @Schema(description = "Status of the watchlist item", example = "PLAN_TO_WATCH")
    WatchStatus watchStatus;
    @Schema(description = "Priority of the watchlist item", example = "MEDIUM")
    Priority priority;
}
