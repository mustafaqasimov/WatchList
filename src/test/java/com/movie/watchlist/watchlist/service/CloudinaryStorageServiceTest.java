package com.movie.watchlist.watchlist.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.movie.watchlist.exception.error.FileStorageException;
import com.movie.watchlist.service.impl.CloudinaryStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryStorageServiceTest {

    @Mock
    private Cloudinary cloudinary;
    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryStorageService storageService;

    // ---------- upload ----------

    @Test
    void upload_returnsSecureUrl_whenFileIsValid() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-image-bytes".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/avatars/abc123.jpg"));

        String result = storageService.upload(file, "avatars");

        assertThat(result).isEqualTo("https://res.cloudinary.com/demo/avatars/abc123.jpg");
    }

    @Test
    void upload_passesCorrectFolderToCloudinary() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "fake-bytes".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap()))
                .thenReturn(Map.of("secure_url", "https://res.cloudinary.com/demo/avatars/xyz.png"));

        storageService.upload(file, "avatars");

        verify(uploader).upload(any(byte[].class), argThat(options ->
                "avatars".equals(options.get("folder")) &&
                        Boolean.TRUE.equals(options.get("overwrite")) &&
                        "image".equals(options.get("resource_type"))
        ));
    }

    @Test
    void upload_throwsIllegalArgumentException_whenFileIsNull() {
        assertThrows(IllegalArgumentException.class, () -> storageService.upload(null, "avatars"));
        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_throwsIllegalArgumentException_whenFileIsEmpty() {
        MultipartFile emptyFile = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        assertThrows(IllegalArgumentException.class, () -> storageService.upload(emptyFile, "avatars"));
        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_throwsIllegalArgumentException_whenContentTypeNotAllowed() {
        MultipartFile pdfFile = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "fake-bytes".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> storageService.upload(pdfFile, "avatars"));
        assertThat(ex.getMessage()).contains("JPEG, PNG or WEBP");
        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_throwsIllegalArgumentException_whenFileExceedsMaxSize() {
        byte[] oversizedContent = new byte[6 * 1024 * 1024]; // 6MB > 5MB limit
        MultipartFile largeFile = new MockMultipartFile(
                "file", "big.jpg", "image/jpeg", oversizedContent);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> storageService.upload(largeFile, "avatars"));
        assertThat(ex.getMessage()).contains("5MB");
        verifyNoInteractions(cloudinary);
    }

    @Test
    void upload_throwsFileStorageException_whenCloudinaryThrowsIOException() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "fake-bytes".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), anyMap())).thenThrow(new IOException("Network error"));

        assertThrows(FileStorageException.class, () -> storageService.upload(file, "avatars"));
    }

    // ---------- delete ----------

    @Test
    void delete_callsCloudinaryDestroy_withGivenPublicId() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq("avatars/abc123"), anyMap())).thenReturn(Map.of("result", "ok"));

        storageService.delete("avatars/abc123");

        verify(uploader).destroy(eq("avatars/abc123"), anyMap());
    }

    @Test
    void delete_throwsFileStorageException_whenCloudinaryThrowsIOException() throws IOException {
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(anyString(), anyMap())).thenThrow(new IOException("Network error"));

        assertThrows(FileStorageException.class, () -> storageService.delete("avatars/abc123"));
    }
}
