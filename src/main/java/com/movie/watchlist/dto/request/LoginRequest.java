package com.movie.watchlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Login request DTO")
public class LoginRequest {

    @Schema(description = "The username of the user",example = "mustafa_qasimov")
    @NotBlank(message = "Username cannot be blank")
    String userName;

    @Schema(description = "The password of the user",example = "password123")
    @NotBlank(message = "Password cannot be blank")
    String password;

    @Schema(description = "The email of the user",example = "mustafaqasimov2413@example.com")
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email format is not valid")
    String email;
}
