package com.movie.watchlist.service.interfaces;

public interface EmailService {
    void sendVerificationEmail(String to, String rawToken);
    void sendPasswordResetEmail(String to, String rawToken);
}
