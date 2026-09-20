package com.movie.watchlist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "Serverin aktivliyini və sağlamlığını yoxlamaq üçün API")
public class HealthController {

    @GetMapping
    @Operation(
            summary = "Serverin sağlamlıq statusunu qaytarır",
            description = "Cron-job və ya uptime monostorinq xidmətləri üçün bazaya müraciət etmədən 200 OK qaytaran yüngül endpoint."
    )
    @ApiResponse(responseCode = "200", description = "Server aktivdir və sorğuları qəbul edir")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Server aktivdir!");
    }
}
