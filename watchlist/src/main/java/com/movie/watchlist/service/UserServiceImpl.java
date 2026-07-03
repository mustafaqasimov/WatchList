package com.movie.watchlist.service;

import com.movie.watchlist.dto.UserRequestDto;
import com.movie.watchlist.dto.UserResponseDto;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    UserMapper userMapper;

    @Override
    public UserResponseDto CreateUser(UserRequestDto user) {
        User entity = userMapper.toEntity(user);
        User savedUser = userRepository.save(entity);
        return userMapper.toDto(savedUser);
    }
}
