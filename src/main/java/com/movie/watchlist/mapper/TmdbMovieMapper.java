package com.movie.watchlist.mapper;

import com.movie.watchlist.dto.records.TmdbMovie;
import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.enums.Genre;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface TmdbMovieMapper {

    @Mapping(target = "movieName", source = "tmdb.title")
    @Mapping(target = "description", source = "tmdb.overview")
    @Mapping(target = "releaseDate", source = "tmdb.release_date", qualifiedByName = "parseDate")
    @Mapping(target = "rating", source = "tmdb.vote_average")
    @Mapping(target = "genre", expression = "java(resolveGenre(tmdb, genreMap))")
    @Mapping(target = "posterPath", source = "tmdb.poster_path")
    @Mapping(target = "tmdbId", source = "tmdb.id")
    MovieRequest toMovieRequest(TmdbMovie tmdb, @Context Map<Integer, String> genreMap);

    @Named("parseDate")
    default LocalDate parseDate(String date) {
        return (date == null || date.isBlank()) ? LocalDate.now() : LocalDate.parse(date);
    }

    default Genre resolveGenre(TmdbMovie tmdb, @Context Map<Integer, String> genreMap) {
        String name = (tmdb.genres() != null && !tmdb.genres().isEmpty())
                ? tmdb.genres().getFirst().name()
                : (tmdb.genre_ids() != null && !tmdb.genre_ids().isEmpty()
                ? genreMap.get(tmdb.genre_ids().getFirst())
                : null);

        if (name == null) return Genre.OTHER;
        try {
            return Genre.valueOf(name.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return Genre.OTHER;
        }
    }
}
