package com.movie.watchlist.entity.entities;

import com.movie.watchlist.entity.base.BaseEntity;
import com.movie.watchlist.enums.Genre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Table(name = "movies")
public class Movie extends BaseEntity {

    @Column(name = "movie_name", length = 255, nullable = false)
    String movieName;

    @Column(name = "description", length = 1000)
    String description;

    @Column(name = "release_date")
    LocalDate releaseDate;

    @Column(name = "rating")
    Double rating;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    Genre genre;

    @Column(name = "poster_path")
    String posterPath;

    @Column(name = "tmdb_id", unique = true)
    Long tmdbId;
}
