package com.movie.watchlist.controller;

import com.movie.watchlist.service.impl.MovieImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
@Tag(name = "Admin Movie Management", description = "Endpoints for admin users to manage movies")
public class AdminMovieController {

    private final MovieImportService importService;

    @Operation(summary = "Import popular movies", description = "Import popular movies from TMDB")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movies imported successfully")
    })
    @PostMapping("/import/popular")
    public ResponseEntity<String> importPopular(@RequestParam(defaultValue = "1") int page) {
        int count = importService.importPopularMovies(page);
        return ResponseEntity.ok(count + " new movies imported");
    }
}
