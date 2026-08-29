package com.movie.watchlist.dto;

import com.movie.watchlist.enums.Genre;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponseDTO {
    Long id;
    String description;
    LocalDate releaseDate;
    Double rating;
    Genre genre;
}
