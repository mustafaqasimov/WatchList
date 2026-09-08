package com.movie.watchlist.controller;

import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.service.interfaces.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
@Tag(name = "Movies", description = "Endpoints for managing movies")
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "Get movie by ID", description = "Retrieves a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @Operation(summary = "Get popular movies", description = "Retrieves a list of popular movies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Popular movies retrieved successfully")
    })
    @GetMapping("/popular")
    public ResponseEntity<Page<MovieResponse>> getPopularMovies(
            @PageableDefault(size = 20) @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(movieService.getPopularMovies(pageable));
    }
}
