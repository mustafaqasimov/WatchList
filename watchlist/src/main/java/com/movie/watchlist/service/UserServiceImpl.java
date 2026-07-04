package com.movie.watchlist.service;

import com.movie.watchlist.dto.UserRequestDto;
import com.movie.watchlist.dto.UserResponseDto;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto user) {
        User entity = userMapper.toEntity(user);
        User savedUser = userRepository.save(entity);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User entity = userRepository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toDto(entity);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAllByActiveStatus(ActiveStatus.ACTIVE);
        return users.stream().map(userMapper::toDto).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        User entity = userRepository.findByIdAndActiveStatus(id, ActiveStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        entity.setActiveStatus(ActiveStatus.INACTIVE);
        userRepository.save(entity);
    }
}
