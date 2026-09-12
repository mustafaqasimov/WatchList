package com.movie.watchlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Schema(description = "Reset password request DTO")
public class ResetPasswordRequest {

    @Schema(description = "Password reset token sent via email")
    @NotBlank(message = "Token is required")
    String token;

    @Schema(description = "New password", example = "newPassword123")
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String newPassword;

    @Schema(description = "Confirm new password", example = "newPassword123")
    @NotBlank(message = "Confirm password is required")
    String confirmNewPassword;
}
