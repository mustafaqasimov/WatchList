package com.movie.watchlist.service;

import com.movie.watchlist.dto.UserRequestDto;
import com.movie.watchlist.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    void deleteUser(Long id);
}
