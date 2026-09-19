package com.movie.watchlist.dto.response;

import com.movie.watchlist.enums.Genre;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Movie response DTO")
public class MovieResponse {
    @Schema(description = "The ID of the movie", example = "1")
    Long id;
    @Schema(description = "The name of the movie", example = "The Matrix")
    String movieName;
    @Schema(description = "The description of the movie", example = "A thrilling action movie")
    String description;
    @Schema(description = "The release date of the movie", example = "2023-01-01")
    LocalDate releaseDate;
    @Schema(description = "The rating of the movie", example = "8.5")
    Double rating;
    @Schema(description = "The genre of the movie", example = "ACTION")
    Genre genre;
    @Schema(example = "/qCzXz5nWXjkVjqhaAcQtoT8AbPh.jpg", description = "TMDB poster path")
    String posterPath;
}
