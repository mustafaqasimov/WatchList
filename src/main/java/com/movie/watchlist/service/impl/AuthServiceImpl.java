package com.movie.watchlist.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.movie.watchlist.dto.request.LoginRequest;
import com.movie.watchlist.dto.request.RegisterRequest;
import com.movie.watchlist.dto.response.AuthResponse;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.exception.error.InvalidCredentialsException;
import com.movie.watchlist.exception.error.ResourceAlreadyExistsException;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.security.JwtService;
import com.movie.watchlist.service.interfaces.AuthImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        userRepository.save(
                userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()))
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return userMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    // 3. Token Yeniləmə (Refresh)
    public AuthResponse refreshToken(String refreshToken) {
        try {
            DecodedJWT decoded = jwtService.verifyToken(refreshToken);
            Long userId = Long.parseLong(decoded.getSubject());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            return userMapper.toAuthResponse(user, newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Refresh token is invalid. Please login again!");
        }
    }
}
