package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.response.UserResponse;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.service.interfaces.StorageService;
import com.movie.watchlist.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        String avatarUrl = storageService.upload(file, "avatars");
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        log.info("Avatar updated for user ID: {}", userId);
        return userMapper.toResponse(user);
    }
}
