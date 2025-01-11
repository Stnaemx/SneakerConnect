package com.Sneaker.SneakerConnect.auth.service;

import com.Sneaker.SneakerConnect.entity.Shop;
import com.Sneaker.SneakerConnect.entity.UsersShop;
import com.Sneaker.SneakerConnect.repository.ShopRepository;
import com.Sneaker.SneakerConnect.repository.UsersShopRepository;
import com.Sneaker.SneakerConnect.user.Role;
import com.Sneaker.SneakerConnect.repository.UserRepository;
import com.Sneaker.SneakerConnect.auth.dto.AuthenticationRequest;
import com.Sneaker.SneakerConnect.auth.dto.RegisterRequest;
import com.Sneaker.SneakerConnect.config.ApplicationConfig;
import com.Sneaker.SneakerConnect.config.JwtService;
import com.Sneaker.SneakerConnect.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ApplicationConfig applicationConfig;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ShopRepository shopRepository;
    private final UsersShopRepository usersShopRepository;

    // Register the user and returns a list of cookies to be returned in HTTP response
    public List<String> register(RegisterRequest registerRequest) {
        Optional<Shop> existingShop = shopRepository.findByName(registerRequest.getShopName());

        // franchises creation cannot be done during sign up
        if(existingShop.isPresent()) {
            throw new IllegalArgumentException("A shop with the name '"
                    + registerRequest.getShopName() + "' already exists. Please choose a different name.");
        }

        // create and save the user
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.OWNER)
                .build();

        var shop = Shop.builder()
                .name(registerRequest.getShopName())
                .address(registerRequest.getAddress())
                .timestamp(LocalDateTime.now())
                .build();

        var usersShop = UsersShop.builder()
                .isOwner(true)
                .email(registerRequest.getEmail())
                .user(user)
                .shop(shop)
                .build();

        userRepository.save(user);
        shopRepository.save(shop);
        usersShopRepository.save(usersShop);

        // set the security context for the current user via their email
        applicationConfig.setSecurityContext(new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities()));

        // generate a list of tokens (access and refresh tokens)
        return generateAccessAndRefreshCookies(user);
    }

    public List<String> authenticate(AuthenticationRequest authenticationRequest) {
        // authenticationManager will use the daoAuthProvider bean to authenticate user
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        // set the security context for the current user via their email
        applicationConfig.setSecurityContext(authentication);

        var user = (UserDetails) authentication.getPrincipal();

        // return a list of both tokens
        return generateAccessAndRefreshCookies(user);
    }

    // Authenticate the user and returns a list of cookies to be returned in HTTP response
    public List<String> generateAccessAndRefreshCookies(UserDetails user) {
        // prepare extra claims (refresh token's exp time)
        Map<String, Object> extraClaims = new HashMap<>();
        String refreshToken = java.util.UUID.randomUUID().toString();

        // save refresh token to redis
        refreshTokenService.createAndStoreRefreshTokenWithExpiration(refreshToken, user.getUsername());

        // Add roles as extra claims by transforming getAuthorities() into a List
        extraClaims.put("roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );

        // generate cookies
        String accessTokenCookie = generateCookie("access_token", jwtService.generateAccessToken(user.getUsername(), extraClaims), Duration.ofMinutes(15));
        String refreshTokenCookie = generateCookie("refresh_token", refreshToken, Duration.ofDays(30));
        return List.of(accessTokenCookie, refreshTokenCookie);
    }

    private String generateCookie(String key, String value, Duration expirationTime) {
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .maxAge(expirationTime)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
        return cookie.toString();
    }
}
