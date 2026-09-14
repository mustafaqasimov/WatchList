package com.movie.watchlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "User profile response DTO")
public class UserResponse {

    @Schema(description = "User ID", example = "1")
    Long id;

    @Schema(description = "Username", example = "john_doe")
    String userName;

    @Schema(description = "Email address", example = "john@example.com")
    String email;

    @Schema(description = "Whether the email has been verified", example = "true")
    boolean emailVerified;

    @Schema(description = "Avatar image URL", example = "https://res.cloudinary.com/.../avatar.jpg")
    String avatarUrl;

    @Schema(description = "Account creation timestamp")
    LocalDateTime createdAt;
}
