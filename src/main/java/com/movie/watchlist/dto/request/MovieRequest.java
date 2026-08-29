package com.movie.watchlist.dto.request;

import com.movie.watchlist.enums.Genre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieRequest {

    @NotBlank(message = "Description is required")
    String description;

    @NotBlank(message = "Release date is required")
    LocalDate releaseDate;

    @Max(value = 10, message = "Rating must be between 0 and 10")
    @NotNull(message = "Rating is required")
    Double rating;

    @NotBlank(message = "Genre is required")
    Genre genre;
}
