package com.movie.watchlist.dto;

import com.movie.watchlist.enums.ActiveStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {

    Long id;
    String username;
    String email;
    String password;
    ActiveStatus activeStatus;
}
