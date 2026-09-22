package com.movie.watchlist.controller;

import com.movie.watchlist.dto.records.ReviewUpdateRequest;
import com.movie.watchlist.dto.request.ReviewRequest;
import com.movie.watchlist.dto.response.ReviewResponse;
import com.movie.watchlist.security.CustomUserDetails;
import com.movie.watchlist.service.interfaces.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Endpoints for managing reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Submit a new review for a movie", description = "Allows users to submit a new review for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @PostMapping("/movies/{movieId}")
    public ResponseEntity<ReviewResponse> createReview(
            @PathVariable Long movieId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ReviewRequest request
    ) {
        ReviewResponse response = reviewService.submitReview(
                principal.getId(),
                movieId,
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Update an existing review", description = "Allows users to update their existing review for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/movies/{movieId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long movieId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {
        ReviewResponse response = reviewService.updateReview(
                principal.getId(),
                movieId,
                request
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get reviews for a specific movie", description = "Retrieves a paginated list of reviews for a given movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsForMovie(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "Page number must be a non-negative integer")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Page size must be a positive integer")
            @Max(value = 100, message = "Page size must not exceed 100")
            int size
    ) {
        Page<ReviewResponse> response =
                reviewService.getReviewsForMovie(movieId, page, size);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Delete a review", description = "Allows users to delete their review for a specific movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/movies/{movieId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long movieId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        reviewService.deleteReview(
                principal.getId(),
                movieId
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a user's review for a specific movie", description = "Retrieves the logged-in user's review for a given movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
        @GetMapping("/movies/{movieId}/my")
    public ResponseEntity<ReviewResponse> getMyReview(
            @PathVariable Long movieId,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ReviewResponse response = reviewService.getMyReview(
                principal.getId(),
                movieId
        );

        return ResponseEntity.ok(response);
    }
}

