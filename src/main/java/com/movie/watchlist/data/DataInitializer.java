package com.movie.watchlist.data;

import com.movie.watchlist.entity.entities.User;
import com.movie.watchlist.enums.Role;
import com.movie.watchlist.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String @NonNull ... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user '{}' already exists, skipping initialization", adminEmail);
            return;
        }

        User admin = User.builder()
                .userName(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ROLE_ADMIN)
                .build();

        userRepository.save(admin);
        log.info("Initial admin user '{}' created", adminUsername);
    }
}
