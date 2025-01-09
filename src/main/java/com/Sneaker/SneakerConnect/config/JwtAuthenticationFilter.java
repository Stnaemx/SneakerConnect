package com.Sneaker.SneakerConnect.config;

import com.Sneaker.SneakerConnect.auth.RefreshTokenService;
import com.Sneaker.SneakerConnect.user.CustomUserDetailsService;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final CustomUserDetailsService customUserDetailsService;

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
        if(refreshToken == null) {
            respondWithUnauthorized(response, "Refresh token missing.");
            return;
        }

        System.out.println("acc: "+accessToken);
        System.out.println("ref: "+refreshToken);

        String userEmail = null;
        UserDetails user = null;

        boolean refreshTokenExpired = !refreshTokenService.keyExist(refreshToken);

        /* 2 cases
        1. access token expired
        2. refresh token has expired
         */
        if(refreshTokenExpired) {
            // modify response and continue filter chain
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            filterChain.doFilter(request, response);
            return;
        }
        else if(accessToken == null) {  // begin refresh token process
            userEmail = refreshTokenService.getUserEmail(refreshToken); // retrieve user from redis
            user = customUserDetailsService.loadUserByUsername(userEmail);
            // updates the http response with a new access token
            refreshAccessToken(response, userEmail, user.getAuthorities());
        }

        // sets the security context for current user
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            // create a token representing the authenticated user and pass this token to the security context holder
            // Store authentication object in securityContext
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail,
                    null,
                    user.getAuthorities()
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

    private void respondWithUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
    }

    private void refreshAccessToken(HttpServletResponse response, String userEmail, Collection<? extends GrantedAuthority> userRoles) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles", userRoles);

        String newAccessToken = jwtService.generateAccessToken(userEmail, extraClaims);

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // specify the endpoints to skip
        return path.startsWith("/api/v1/auth");
    }
}
