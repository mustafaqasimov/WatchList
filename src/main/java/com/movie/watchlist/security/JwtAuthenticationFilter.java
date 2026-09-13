package com.movie.watchlist.security;

import com.movie.watchlist.service.impl.TokenBlacklistService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // STEP 1: Header yoxlanışı
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error(">>> [STEP 1 FAILED] Header missing or not starting with Bearer. URI: {}, Header: {}",
                    request.getRequestURI(), authHeader);
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // STEP 2: Blacklist yoxlanışı
            boolean isBlacklisted = blacklistService.isBlacklisted(token);
            if (isBlacklisted) {
                log.error(">>> [STEP 2 FAILED] Token is blacklisted for URI: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // STEP 3: Token Validasiyası
            boolean isAccess = jwtService.isAccessToken(token);
            boolean isValid = jwtService.isTokenValid(token);

            log.error(">>> [STEP 3 CHECK] isAccessToken: {}, isTokenValid: {}", isAccess, isValid);

            if (isAccess && isValid) {
                String email = jwtService.extractEmail(token);
                var currentAuth = SecurityContextHolder.getContext().getAuthentication();

                log.error(">>> [STEP 4 CHECK] Extracted Email: {}, Current Context Auth: {}", email, currentAuth);

                if (email != null && currentAuth == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.error(">>> [SUCCESS] Authenticated User: {}, Authorities: {}", email, userDetails.getAuthorities());
                }
            } else {
                log.error(">>> [STEP 3 FAILED] Token is invalid or not an access token");
            }
        } catch (Exception ex) {
            log.error(">>> [EXCEPTION THROWN] JWT processing failed for URI '{}': ", request.getRequestURI(), ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
