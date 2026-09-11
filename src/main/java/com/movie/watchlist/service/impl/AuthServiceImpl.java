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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String DUMMY_HASH = "$2a$12$yhnofnEDmL7otif7y1unQuNw8JFM4eFe4GafBW78DVKyA9Gpil4SS";

    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        User savedUser = userRepository.save(
                userMapper.toEntity(request, passwordEncoder.encode(request.getPassword()))
        );

        log.info("User registered successfully with ID: {}", savedUser.getId());
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user != null ? user.getPassword() : DUMMY_HASH
        );

        if (user == null || !passwordMatches) {
            log.warn("Failed login attempt for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (user.getActiveStatus() != ActiveStatus.ACTIVE) {
            log.warn("Inactive account login attempt for email: {}", request.getEmail());
            throw new InvalidCredentialsException("Account is not active");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        log.info("User successfully logged in with ID: {}", user.getId());
        return userMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refreshAccessToken(String rawRefreshToken) {
        if (!jwtService.isTokenValid(rawRefreshToken) || !jwtService.isRefreshToken(rawRefreshToken)) {
            log.warn("Invalid refresh token structure provided");
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        Long userId = jwtService.extractUserId(rawRefreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Refresh token user not found with ID: {}", userId);
                    return new InvalidCredentialsException("Invalid refresh token");
                });

        String hashedInputToken = hashToken(rawRefreshToken);
        List<RefreshToken> activeTokens = refreshTokenRepository.findAllByUserAndRevokedFalse(user);

        // DÜZƏLİŞ: SHA-256 heşi birbaşa string equals ilə yoxlanılır (BCrypt əvəzinə)
        boolean tokenExists = activeTokens.stream()
                .anyMatch(rt -> rt.getToken().equals(hashedInputToken)
                        && rt.getExpiryDate().isAfter(Instant.now()));

        if (!tokenExists) {
            log.warn("Refresh token for user ID {} is expired or revoked", userId);
            throw new InvalidCredentialsException("Refresh token expired or revoked");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        log.info("Access token successfully refreshed for user ID: {}", user.getId());

        return userMapper.toAuthResponse(user, newAccessToken, rawRefreshToken);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        String hashedToken = hashToken(refreshToken);

        RefreshToken tokenEntity = RefreshToken.builder()
                .user(user)
                .token(hashedToken)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .activeStatus(ActiveStatus.ACTIVE)
                .revoked(false)
                .build();

        refreshTokenRepository.save(tokenEntity);
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = extractToken(request);

        if (token != null && jwtService.isTokenValid(token)) {
            Date expiration = jwtService.extractExpiration(token);
            tokenBlacklistService.blacklist(token, expiration);
            log.info("Token blacklisted successfully during logout");
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
            log.error("SHA-256 algorithm not available for token hashing", e);
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
