package com.movie.watchlist.controller;

import com.movie.watchlist.dto.request.ChangePasswordRequest;
import com.movie.watchlist.dto.response.UserResponse;
import com.movie.watchlist.security.CustomUserDetails;
import com.movie.watchlist.service.interfaces.AuthService;
import com.movie.watchlist.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing the current user's profile")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "Get current user's profile", description = "Retrieve the profile information of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getId()));
    }

    @Operation(summary = "Change password", description = "Update the password of the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid current password")
    })
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload avatar", description = "Upload a new avatar for the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avatar uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file format")
    })
    @PostMapping(
            value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> updateAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.updateAvatar(userDetails.getId(), file));
    }
}
