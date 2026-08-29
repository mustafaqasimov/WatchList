package com.movie.watchlist.controller;

import com.movie.watchlist.dto.MovieRequestDTO;
import com.movie.watchlist.dto.MovieResponseDTO;
import com.movie.watchlist.service.MovieServiceImpl;
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
    public ResponseEntity<MovieResponseDTO> addMovie(@Valid @RequestBody MovieRequestDTO requestDTO){
        MovieResponseDTO responseDTO = movieService.addMovie(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovie(@PathVariable Long id) {
        MovieResponseDTO responseDTO = movieService.getMovieById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping()
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        List<MovieResponseDTO> responseDTO = movieService.getAllMovies();
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
