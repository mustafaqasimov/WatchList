package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse getProfile(Long userId);
    UserResponse updateAvatar(Long userId, MultipartFile file);
}
