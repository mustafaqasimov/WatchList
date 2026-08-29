package com.movie.watchlist.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "Username cannot be blank")
    String userName;

    @NotBlank(message = "Password cannot be blank")
    String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email format is not valid")
    String email;
}
