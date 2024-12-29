package com.Sneaker.SneakerConnect.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationConfig applicationConfig;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String headerCookie = request.getHeader("Cookie");
        if(headerCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }
        final String access_token = extractToken(headerCookie, "access_token");
        // could throw exception if token validation fails
        Claims tokenClaims = jwtService.extractClaims(access_token);
        // check if SecurityContext has been set (shouldn't be)
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            // create a token representing the authenticated user and pass this token to the security context holder
            var userEmail = tokenClaims.get("sub");
            // Store authentication object in securityContext
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail,
                    null,
                    jwtService.extractRoles(tokenClaims)
            );
            // pass additional metadata to token
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // create a new SecurityContext to avoid race conditions
            applicationConfig.setSecurityContext(authToken);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(String headerCookie, String tokenType) {
        return Arrays.stream(headerCookie.split(";"))
                .filter(token -> token.startsWith(tokenType + "="))
                .map(token -> token.substring((tokenType + "=").length()))
                .findFirst()
                .orElse(null);
    }
}
