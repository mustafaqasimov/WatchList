package com.movie.watchlist.dto.records;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record EmailRequest(
        @Schema(description = "Email address of the sender")
        String from,
        @Schema(description = "List of email addresses of the recipients")
        List<String> to,
        @Schema(description = "Subject of the email")
        String subject,
        @Schema(description = "HTML content of the email")
        String html
) {}
