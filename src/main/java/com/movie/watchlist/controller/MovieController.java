package com.movie.watchlist.controller;

import com.movie.watchlist.dto.request.MovieRequest;
import com.movie.watchlist.dto.response.MovieResponse;
import com.movie.watchlist.service.impl.MovieServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
@RequiredArgsConstructor
public class MovieController {

    private final MovieServiceImpl movieService;

    @PostMapping("/add")
    public ResponseEntity<MovieResponse> addMovie(@Valid @RequestBody MovieRequest requestDTO){
        MovieResponse responseDTO = movieService.addMovie(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable Long id) {
        MovieResponse responseDTO = movieService.getMovieById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping()
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> responseDTO = movieService.getAllMovies();
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
