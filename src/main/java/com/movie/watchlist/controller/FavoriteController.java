package com.movie.watchlist.controller;

import com.movie.watchlist.dto.response.FavoriteResponse;
import com.movie.watchlist.security.CustomUserDetails;
import com.movie.watchlist.service.interfaces.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints for managing the user's favorite movies")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "Add movie to favorites")
    @PostMapping("/{tmdbId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long tmdbId) {
        FavoriteResponse response = favoriteService.addFavorite(principal.getId(), tmdbId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get current user's favorite movies")
    @GetMapping
    public ResponseEntity<Page<FavoriteResponse>> getFavorites(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PageableDefault(size = 20) @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(favoriteService.getFavorites(principal.getId(), pageable));
    }

    @Operation(summary = "Remove movie from favorites")
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long tmdbId) {
        favoriteService.removeFavorite(principal.getId(), tmdbId);
        return ResponseEntity.noContent().build();
    }
}
