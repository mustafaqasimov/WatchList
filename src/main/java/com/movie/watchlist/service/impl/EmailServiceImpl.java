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

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.resend.com")
            .build();

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from}")
    private String resendFrom;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Async
    public void sendVerificationEmail(String to, String rawToken) {
        String link = frontendUrl + "/verify-email?token=" + rawToken;

        String htmlBody =
                "Hello, please click the link to verify your email: "
                        + "<a href='" + link + "'>Verify Here</a>"
                        + "<br><br>"
                        + "The link is valid for 24 hours.";

        send(to, "Verify Email", htmlBody);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String rawToken) {
        String link = frontendUrl + "/reset-password?token=" + rawToken;

        String htmlBody =
                "Please click the link to reset your password: "
                        + "<a href='" + link + "'>Reset Password</a>"
                        + "<br><br>"
                        + "Link 15 dəqiqə ərzində etibarlıdır.";

        send(to, "Password Reset", htmlBody);
    }

    private void send(String to, String subject, String htmlBody) {
        EmailRequest requestBody = new EmailRequest(
                resendFrom,
                List.of(to),
                subject,
                htmlBody
        );

        restClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + resendApiKey.trim())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}
