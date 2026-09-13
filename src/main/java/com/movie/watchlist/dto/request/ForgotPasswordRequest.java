package com.movie.watchlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Schema(description = "Forgot password request DTO")
public class ForgotPasswordRequest {

    @Schema(description = "The email address of the user who forgot their password")
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;
}
