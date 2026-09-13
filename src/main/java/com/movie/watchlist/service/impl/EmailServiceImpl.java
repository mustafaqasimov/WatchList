package com.movie.watchlist.service.impl;

import com.movie.watchlist.dto.records.EmailRequest;
import com.movie.watchlist.service.interfaces.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // Spring Boot 3-ün yeni RestClient funksionallığı vasitəsilə Resend API-yə qoşuluruq
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.resend.com")
            .build();

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Async
    public void sendVerificationEmail(String to, String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;

        // Mətni HTML formatında göndəririk ki, istifadəçi linkə birbaşa klikləyə bilsin
        String htmlBody = "Hello, please click the link to verify your email: <a href='" + link + "'>Verify Here</a>" +
                "<br><br>" +
                "The link is valid for 24 hours.";

        send(to, "Verify Email", htmlBody);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;

        String htmlBody = "Please click the link to reset your password: <a href='" + link + "'>Reset Password</a>" +
                "<br><br>" +
                "Link 15 dəqiqə ərzində etibarlıdır. Əgər siz bu sorğunu göndərməmisinizsə, bu emaili yox sayın.";

        send(to, "Password Reset", htmlBody);
    }

    private void send(String to, String subject, String htmlBody) {
        var requestBody = new EmailRequest(
                "onboarding@resend.dev",
                List.of(to),
                subject,
                htmlBody
        );

        restClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}
