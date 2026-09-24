package com.movie.watchlist.dto.response;

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
@Schema(description = "Watchlist response DTO")
public class WatchlistResponse {
    @Schema(description = "Watchlist ID")
    Long id;
    @Schema(description = "Watchlist name")
    String name;
    @Schema(description = "Number of items in the watchlist")
    int itemCount;
    @Schema(description = "Creation timestamp")
    LocalDateTime createdAt;
}
