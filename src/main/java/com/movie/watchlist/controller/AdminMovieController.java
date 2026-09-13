package com.movie.watchlist.controller;

import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.entity.entities.Movie;
import com.movie.watchlist.mapper.MovieMapper;
import com.movie.watchlist.service.impl.MovieImportService;
import com.movie.watchlist.service.interfaces.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/movies")
@RequiredArgsConstructor
@Tag(name = "Admin Movie Management", description = "Endpoints for admin users to manage movies")
public class AdminMovieController {

    private final MovieImportService importService;
    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @Operation(summary = "Import popular movies", description = "Import popular movies from TMDB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movies imported successfully")
    })
    @PostMapping("/import/popular")
    public ResponseEntity<String> importPopular(@RequestParam(defaultValue = "1") int page) {
        int count = importService.importPopularMovies(page);
        return ResponseEntity.ok(count + " new movies imported");
    }

    @PostMapping("/import/{tmdbId}")
    public ResponseEntity<MovieResponse> importMovie(@PathVariable long tmdbId) {

        Movie movie = importService.getOrImportMovie(tmdbId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movieMapper.toDTO(movie));
    }

    @Operation(summary = "Delete a movie", description = "Deletes a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
