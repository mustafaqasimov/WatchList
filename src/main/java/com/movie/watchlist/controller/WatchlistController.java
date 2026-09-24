package com.movie.watchlist.controller;

import com.movie.watchlist.dto.records.WatchlistUpdateRequest;
import com.movie.watchlist.dto.request.WatchlistItemRequest;
import com.movie.watchlist.dto.request.WatchlistRequest;
import com.movie.watchlist.dto.response.WatchlistItemResponse;
import com.movie.watchlist.dto.response.WatchlistResponse;
import com.movie.watchlist.security.CustomUserDetails;
import com.movie.watchlist.service.interfaces.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/watchlists")
@RequiredArgsConstructor
@Tag(name = "Watchlists", description = "Watchlist management endpoints")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @Operation(summary = "Create a new watchlist", description = "Creates a new watchlist for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Watchlist created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<WatchlistResponse> createWatchlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody WatchlistRequest request) {
        WatchlistResponse response = watchlistService.createWatchlist(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user's watchlists", description = "Retrieves all watchlists for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlists retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getUserWatchlists(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<WatchlistResponse> responses = watchlistService.getUserWatchlists(currentUser.getId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Delete a watchlist", description = "Deletes a watchlist for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Watchlist deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Watchlist not found")
    })
    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long watchlistId) {
        watchlistService.deleteWatchlist(currentUser.getId(), watchlistId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a movie to a watchlist", description = "Adds a movie to the specified watchlist for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie added to watchlist successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Watchlist not found")
    })
    @PostMapping("/{watchlistId}/movies")
    public ResponseEntity<WatchlistItemResponse> addMovieToWatchlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long watchlistId,
            @Valid @RequestBody WatchlistItemRequest request) {
        WatchlistItemResponse response = watchlistService.addMovieToWatchlist(currentUser.getId(), watchlistId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get movies in a watchlist", description = "Retrieves all movies in the specified watchlist for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movies retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Watchlist not found")
    })
    @GetMapping("/{watchlistId}/movies")
    public ResponseEntity<Page<WatchlistItemResponse>> getWatchlistMovies(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long watchlistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WatchlistItemResponse> items = watchlistService.getWatchlistItems(currentUser.getId(), watchlistId, page, size);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Update movie status in a watchlist", description = "Updates the status of a movie in the specified watchlist for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Watchlist or movie not found")
    })
    @PutMapping("/{watchlistId}/movies/{movieId}")
    public ResponseEntity<WatchlistItemResponse> updateMovieStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long watchlistId,
            @PathVariable Long movieId,
            @Valid @RequestBody WatchlistUpdateRequest request) {
        WatchlistItemResponse response = watchlistService.updateMovieStatus(currentUser.getId(), watchlistId, movieId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove a movie from a watchlist", description = "Removes a movie from the specified watchlist for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie removed from watchlist successfully"),
            @ApiResponse(responseCode = "404", description = "Watchlist or movie not found")
    })
    @DeleteMapping("/{watchlistId}/movies/{movieId}")
    public ResponseEntity<Void> removeMovieFromWatchlist(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long watchlistId,
            @PathVariable Long movieId) {
        watchlistService.removeMovieFromWatchlist(currentUser.getId(), watchlistId, movieId);
        return ResponseEntity.noContent().build();
    }
}
