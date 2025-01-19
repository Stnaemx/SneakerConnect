package com.Sneaker.SneakerConnect.auth.service;

import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import com.Sneaker.SneakerConnect.entity.*;
import com.Sneaker.SneakerConnect.entity.userCreation.UserCreationStrategy;
import com.Sneaker.SneakerConnect.entity.userCreation.UserCreationStrategyFactory;
import com.Sneaker.SneakerConnect.repository.UsersShopRepository;
import com.Sneaker.SneakerConnect.auth.dto.AuthenticationRequest;
import com.Sneaker.SneakerConnect.config.ApplicationConfig;
import com.Sneaker.SneakerConnect.config.JwtService;
import com.Sneaker.SneakerConnect.service.CustomUserDetailsService;
import com.Sneaker.SneakerConnect.service.ShopService;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ApplicationConfig applicationConfig;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UsersShopRepository usersShopRepository;
    private final UserShopFactory userShopFactory;
    private final UserCreationStrategyFactory userCreationStrategyFactory;
    private final ShopService shopService;
    private final CustomUserDetailsService customUserDetailsService;

    // Register the user and returns a list of cookies to be returned in HTTP response
    public <T extends UserCreationDto> List<String> register(T registerRequest, Role role) {

        // dynamically decide which user creation strategy (implementation class) to use
        UserCreationStrategy<T> strategy = userCreationStrategyFactory.getStrategy(role);
        User user = strategy.createUser(registerRequest);
        Shop shop = shopService.resolveShop(registerRequest, user.getRole());

        // set the isOwner field in the join table to indicate if the created user is the shop owner
        boolean isOwner = user.getRole() == Role.OWNER;
        UsersShop usersShop = userShopFactory.createUserShop(registerRequest, user, shop, isOwner);

        // both user and shop must exist prior to populating the join table
        customUserDetailsService.saveUser(user);
        usersShopRepository.save(usersShop); // join table

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
        // TODO roles will be sent via http response body instead
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
