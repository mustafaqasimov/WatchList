package com.movie.watchlist.controller;

import com.movie.watchlist.dto.records.TmdbMovieResponse;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.service.impl.MovieImportService;
import com.movie.watchlist.service.impl.TmdbMovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movie Discovery", description = "Endpoints for discovering movies")
public class MovieDiscoveryController {

    private final TmdbMovieService tmdbMovieService;
    private final MovieImportService importService;

    @Operation(summary = "Search for movies", description = "Search for movies based on a query string")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successful search"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    @GetMapping("/search")
    public TmdbMovieResponse search(@RequestParam String query,
                                    @RequestParam(defaultValue = "1") int page) {
        return tmdbMovieService.searchMovies(query, page);
    }

    @Operation(summary = "Add movie from TMDB", description = "Add a movie to the watchlist from TMDB")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Movie added successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PostMapping("/{tmdbId}/add")
    public ResponseEntity<MovieResponse> addFromTmdb(@PathVariable long tmdbId) {
        MovieResponse movie = importService.addSingleMovie(tmdbId);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);
    }
}
