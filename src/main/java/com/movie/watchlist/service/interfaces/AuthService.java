package com.movie.watchlist.service.interfaces;

import com.movie.watchlist.dto.request.LoginRequest;
import com.movie.watchlist.dto.request.RegisterRequest;
import com.movie.watchlist.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
        void register(RegisterRequest request);

        AuthResponse login(LoginRequest request);

        AuthResponse refreshAccessToken(String rawRefreshToken);

        void logout(HttpServletRequest request);
}

