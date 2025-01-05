package com.Sneaker.SneakerConnect.config;

import com.Sneaker.SneakerConnect.auth.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ApplicationConfig applicationConfig;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if(request.getCookies() == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String> tokenMap = extractToken(request.getCookies());
        String accessToken = tokenMap.get("access_token");
        String refreshToken = tokenMap.get("refresh_token");

        // verify tokens are included
        if (accessToken == null || refreshToken == null) {
            respondWithUnauthorized(response, "Access or refresh token missing.");
            return;
        }

        System.out.println("acc: "+accessToken);
        System.out.println("ref: "+refreshToken);
        Claims tokenClaims = null;
        try {
            tokenClaims = jwtService.extractAllClaims(accessToken);
        } catch (JwtException e) {
            respondWithUnauthorized(response, "Invalid or expired access token.");
            return;
        }

        Date accessTokenExp = tokenClaims.getExpiration();
        boolean accessTokenExpired = accessTokenExp.before(new Date());
        String userEmail = tokenClaims.getSubject();

        String refreshTokenExpiration = tokenClaims.get("refreshTokenExpiration", String.class);
        boolean refreshTokenExpired = isRefreshTokenExpired(Long.parseLong(refreshTokenExpiration));
        List<String> userRoles = tokenClaims.get("roles", List.class);

        // access token refresh based refresh token expiration
        if(accessTokenExpired) {
            // both tokens are expired
            if(refreshTokenExpired) {
                // modify response
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                // continue filter chain
                filterChain.doFilter(request, response);
                return;
            }
            // just the access token is expired: meaning refresh the access token
            else {
                refreshAccessToken(response, userEmail, userRoles, refreshTokenExpiration);
            }
        }

        // sets the security context for current user
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            // create a token representing the authenticated user and pass this token to the security context holder
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

    // extract a specific cookie from the header and separates it from other attributes
    private Map<String, String> extractToken(Cookie[] cookies) {
        Map<String, String> cookieMap = new HashMap<>();
        for(Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }
        return cookieMap;
    }

    private boolean isRefreshTokenExpired(long tokenExp) {
        // convert to epoch time in seconds
        return System.currentTimeMillis()/1000 >= tokenExp;
    }

    private void respondWithUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }

    private void refreshAccessToken(HttpServletResponse response, String userEmail, List<String> userRoles, String refreshTokenExpiration) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userRoles);
        extraClaims.put("refreshTokenExpiration", refreshTokenExpiration);

        String newAccessToken = jwtService.generateAccessToken(userEmail, extraClaims);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    }
}
