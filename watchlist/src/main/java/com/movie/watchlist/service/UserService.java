package com.movie.watchlist.service;

import com.movie.watchlist.dto.UserRequestDto;
import com.movie.watchlist.dto.UserResponseDto;

public interface UserService {
    UserResponseDto CreateUser(UserRequestDto userRequestDto);
}
