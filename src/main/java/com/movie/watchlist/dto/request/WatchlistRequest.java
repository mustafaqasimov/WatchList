package com.movie.watchlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Watchlist request DTO")
public class WatchlistRequest {

    @NotBlank(message = "List name is required")
    @Size(max = 100, message = "List name cannot exceed 100 characters")
    @Schema(description = "Name of the watchlist", example = "My Watchlist")
    String name;
}
