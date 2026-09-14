package com.movie.watchlist.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(MultipartFile file, String folder);
    void delete(String publicId);
}
