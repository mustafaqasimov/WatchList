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
@Schema(description = "Review response DTO")
public class ReviewResponse {

    @Schema(description = "Review ID",example = "1")
    Long id;
    @Schema(description = "Review rating",example = "4.5")
    Double rating;
    @Schema(description = "Review content",example = "Great movie!")
    String content;
    @Schema(description = "Author name",example = "John Doe")
    String authorName;
    @Schema(description = "Movie ID",example = "1")
    Long movieId;
    @Schema(description = "Creation timestamp",example = "2023-01-01T12:00:00")
    LocalDateTime createdAt;
}
