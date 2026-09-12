package com.movie.watchlist.service.impl;

import com.movie.watchlist.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Async
    public void sendVerificationEmail(String to, String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;
        send(to, "Verify Email",
                "Hello, please click the link to verify your email: " + link +
                        "\n\n" +
                        "The link is valid for 24 hours.");
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;
        send(to, "Password Reset",
                "Please click the link to reset your password: " + link +
                        "\nLink 15 dəqiqə ərzində etibarlıdır. Əgər siz bu sorğunu göndərməmisinizsə, bu emaili yox sayın.");
    }

    private void send(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
