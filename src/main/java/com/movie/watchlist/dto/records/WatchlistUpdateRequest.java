package com.movie.watchlist.dto.records;

import com.movie.watchlist.enums.Priority;
import com.movie.watchlist.enums.WatchStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record WatchlistUpdateRequest(
        @NotNull(message = "Status is required")
        @Schema(description = "The status of the watchlist item", example = "WATCHED")
        WatchStatus watchStatus,
        @Schema(description = "The priority of the watchlist item", example = "HIGH")
        @NotNull(message = "Priority is required")
        Priority priority
) {
}
