package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.request.LoginRequest;
import com.movie.watchlist.dto.request.RegisterRequest;
import com.movie.watchlist.dto.response.AuthResponse;
import com.movie.watchlist.entity.entities.RefreshToken;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.InvalidCredentialsException;
import com.movie.watchlist.exception.error.ResourceAlreadyExistsException;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.RefreshTokenRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.security.JwtService;
import com.movie.watchlist.service.interfaces.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DUMMY_HASH = "$2a$12$yhnofnEDmL7otif7y1unQuNw8JFM4eFe4GafBW78DVKyA9Gpil4SS";

    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        userRepository.save(
                userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()))
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user != null ? user.getPassword() : DUMMY_HASH
        );

        if (user == null || !passwordMatches) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (user.getActiveStatus() != ActiveStatus.ACTIVE) {
            throw new InvalidCredentialsException("Account is not active");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return userMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refreshAccessToken(String rawRefreshToken) {
        if (!jwtService.isTokenValid(rawRefreshToken) || !jwtService.isRefreshToken(rawRefreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        Long userId = jwtService.extractUserId(rawRefreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        boolean tokenExists = activeTokens.stream()
                .anyMatch(rt -> passwordEncoder.matches(rawRefreshToken, rt.getToken())
                        && rt.getExpiryDate().isAfter(Instant.now()));

        if (!tokenExists) {
            throw new InvalidCredentialsException("Refresh token expired or revoked");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        return userMapper.toAuthResponse(user, newAccessToken, rawRefreshToken);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        String hashedToken = hashToken(refreshToken);

        RefreshToken tokenEntity = RefreshToken.builder()
                .user(user)
                .token(hashedToken)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .activeStatus(ActiveStatus.ACTIVE)
                .build();

        refreshTokenRepository.save(tokenEntity);
    }

    public void logout(HttpServletRequest request) {
        String token = extractToken(request);

        if (token != null && jwtService.isTokenValid(token)) {
            Date expiration = jwtService.extractExpiration(token);
            tokenBlacklistService.blacklist(token, expiration);
        }

        SecurityContextHolder.clearContext();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
