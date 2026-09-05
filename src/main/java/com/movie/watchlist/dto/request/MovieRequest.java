package com.movie.watchlist.dto.request;

import com.movie.watchlist.enums.Genre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Movie request DTO")
public class MovieRequest {

    @NotBlank(message = "Movie name is required")
    @Schema(description = "The name of the movie", example = "The Matrix")
    String movieName;

    @NotBlank(message = "Description is required")
    @Schema(description = "The description of the movie", example = "A thrilling action movie")
    String description;

    @NotNull(message = "Release date is required")
    @PastOrPresent(message = "Release date cannot be in the future")
    @Schema(description = "The release date of the movie", example = "2023-01-01")
    LocalDate releaseDate;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be between 0 and 10")
    @Max(value = 10, message = "Rating must be between 0 and 10")
    @Schema(description = "The rating of the movie", example = "8.5")
    Double rating;

    @NotNull(message = "Genre is required")
    @Schema(description = "The genre of the movie", example = "ACTION")
    Genre genre;

    @Schema(example = "/qCzXz5nWXjkVjqhaAcQtoT8AbPh.jpg", description = "TMDB poster path")
    String posterPath;

    @Schema(example = "27205", description = "TMDB movie Id")
    Long tmdbId;
}
