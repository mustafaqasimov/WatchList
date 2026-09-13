package com.movie.watchlist.watchlist.service;

import com.movie.watchlist.dto.request.ChangePasswordRequest;
import com.movie.watchlist.dto.request.LoginRequest;
import com.movie.watchlist.dto.request.RegisterRequest;
import com.movie.watchlist.dto.response.AuthResponse;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.ActiveStatus;
import com.movie.watchlist.exception.error.InvalidCredentialsException;
import com.movie.watchlist.exception.error.PasswordMismatchException;
import com.movie.watchlist.exception.error.ResourceAlreadyExistsException;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.RefreshTokenRepository;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.repositories.VerificationTokenRepository;
import com.movie.watchlist.security.JwtService;
import com.movie.watchlist.service.impl.AuthServiceImpl;
import com.movie.watchlist.service.impl.TokenBlacklistService;
import com.movie.watchlist.service.interfaces.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private TokenBlacklistService tokenBlacklistService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final String EMAIL = "test@watchlist.com";
    private static final String RAW_PASSWORD = "Password123";
    private static final String ENCODED_PASSWORD = "encoded-hash";

    @BeforeEach
    void setUp() {
        // @Value fields are not populated outside a Spring context - set manually
        ReflectionTestUtils.setField(authService, "verificationTokenExpirationMinutes", 1440);
    }

    // ---------- register ----------

    @Test
    void register_savesUserAndSendsVerificationEmail_whenEmailNotTaken() {
        RegisterRequest request = mock(RegisterRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(request.getPassword()).thenReturn(RAW_PASSWORD);

        User unsavedUser = mock(User.class);
        User savedUser = mock(User.class);
        when(savedUser.getEmail()).thenReturn(EMAIL);
        when(savedUser.getId()).thenReturn(1L);

        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userMapper.toEntity(request, ENCODED_PASSWORD)).thenReturn(unsavedUser);
        when(userRepository.save(unsavedUser)).thenReturn(savedUser);

        authService.register(request);

        verify(emailService).sendVerificationEmail(eq(EMAIL), anyString());
        verify(verificationTokenRepository).save(any());
    }

    @Test
    void register_throwsResourceAlreadyExistsException_whenEmailAlreadyRegistered() {
        RegisterRequest request = mock(RegisterRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendVerificationEmail(anyString(), anyString());
    }

    // ---------- login ----------

    @Test
    void login_returnsAuthResponse_whenCredentialsValidAndAccountVerifiedAndActive() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(request.getPassword()).thenReturn(RAW_PASSWORD);

        User user = mock(User.class);
        when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getActiveStatus()).thenReturn(ActiveStatus.ACTIVE);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        AuthResponse expected = mock(AuthResponse.class);
        when(userMapper.toAuthResponse(user, "access-token", "refresh-token")).thenReturn(expected);

        AuthResponse result = authService.login(request);

        assertThat(result).isEqualTo(expected);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void login_throwsInvalidCredentials_whenUserDoesNotExist() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(request.getPassword()).thenReturn(RAW_PASSWORD);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.matches(eq(RAW_PASSWORD), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_throwsInvalidCredentials_whenPasswordDoesNotMatch() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(request.getPassword()).thenReturn("wrong-password");

        User user = mock(User.class);
        when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_throwsInvalidCredentials_whenEmailNotVerified() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(request.getPassword()).thenReturn(RAW_PASSWORD);

        User user = mock(User.class);
        when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
        when(user.isEmailVerified()).thenReturn(false);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request));
        assertThat(ex.getMessage()).contains("verify your email");
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void login_throwsInvalidCredentials_whenAccountNotActive() {
        LoginRequest request = mock(LoginRequest.class);
        when(request.getEmail()).thenReturn(EMAIL);
        when(request.getPassword()).thenReturn(RAW_PASSWORD);

        User user = mock(User.class);
        when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
        when(user.isEmailVerified()).thenReturn(true);
        when(user.getActiveStatus()).thenReturn(ActiveStatus.INACTIVE);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    // ---------- changePassword ----------

    @Test
    void changePassword_updatesPasswordAndRevokesSessions_whenOldPasswordCorrectAndNewPasswordsMatch() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPass123")
                .newPassword("newPass123")
                .confirmNewPassword("newPass123")
                .build();

        User user = mock(User.class);
        when(user.getPassword()).thenReturn(ENCODED_PASSWORD);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass123", ENCODED_PASSWORD)).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("new-encoded-hash");

        authService.changePassword(1L, request);

        verify(user).setPassword("new-encoded-hash");
        verify(userRepository).save(user);
        verify(refreshTokenRepository).revokeAllByUser(user);
    }

    @Test
    void changePassword_throwsPasswordMismatch_whenNewPasswordsDoNotMatch() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPass123")
                .newPassword("newPass123")
                .confirmNewPassword("different123")
                .build();

        assertThrows(PasswordMismatchException.class, () -> authService.changePassword(1L, request));

        verifyNoInteractions(userRepository);
    }

    @Test
    void changePassword_throwsResourceNotFound_whenUserDoesNotExist() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("oldPass123")
                .newPassword("newPass123")
                .confirmNewPassword("newPass123")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.changePassword(1L, request));
    }

    @Test
    void changePassword_throwsInvalidCredentials_whenOldPasswordIncorrect() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("wrongOldPass")
                .newPassword("newPass123")
                .confirmNewPassword("newPass123")
                .build();

        User user = mock(User.class);
        when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPass", ENCODED_PASSWORD)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.changePassword(1L, request));

        verify(userRepository, never()).save(any());
    }
}
