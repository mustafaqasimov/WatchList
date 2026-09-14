package com.movie.watchlist.watchlist.service;

import com.movie.watchlist.dto.response.UserResponse;
import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.exception.error.ResourceNotFoundException;
import com.movie.watchlist.mapper.UserMapper;
import com.movie.watchlist.repositories.UserRepository;
import com.movie.watchlist.service.impl.UserServiceImpl;
import com.movie.watchlist.service.interfaces.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private UserServiceImpl userService;

    private static final Long USER_ID = 1L;

    // ---------- getProfile ----------

    @Test
    void getProfile_returnsMappedResponse_whenUserExists() {
        User user = mock(User.class);
        UserResponse expectedResponse = mock(UserResponse.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.getProfile(USER_ID);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getProfile_throwsResourceNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(USER_ID));

        verifyNoInteractions(userMapper);
    }

    // ---------- updateAvatar ----------

    @Test
    void updateAvatar_uploadsFileAndUpdatesUser_whenUserExists() {
        User user = mock(User.class);
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-bytes".getBytes());
        UserResponse expectedResponse = mock(UserResponse.class);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(storageService.upload(file, "avatars")).thenReturn("https://res.cloudinary.com/demo/avatars/abc.jpg");
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.updateAvatar(USER_ID, file);

        assertThat(result).isEqualTo(expectedResponse);
        verify(user).setAvatarUrl("https://res.cloudinary.com/demo/avatars/abc.jpg");
        verify(userRepository).save(user);
    }

    @Test
    void updateAvatar_throwsResourceNotFound_whenUserDoesNotExist() {
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-bytes".getBytes());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateAvatar(USER_ID, file));

        verifyNoInteractions(storageService);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateAvatar_doesNotSaveUser_whenStorageServiceThrowsException() {
        User user = mock(User.class);
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-bytes".getBytes());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(storageService.upload(file, "avatars")).thenThrow(new IllegalArgumentException("Invalid file"));

        assertThrows(IllegalArgumentException.class, () -> userService.updateAvatar(USER_ID, file));

        verify(userRepository, never()).save(any());
        verify(user, never()).setAvatarUrl(any());
    }
}
