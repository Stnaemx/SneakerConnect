package com.Sneaker.SneakerConnect.auth;

import com.Sneaker.SneakerConnect.Role;
import com.Sneaker.SneakerConnect.UserRepository;
import com.Sneaker.SneakerConnect.config.ApplicationConfig;
import com.Sneaker.SneakerConnect.config.JwtService;
import com.Sneaker.SneakerConnect.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ApplicationConfig applicationConfig;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseCookie register(RegisterRequest registerRequest) {
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.OWNER)
                .build();
        userRepository.save(user);
        // set the security context for the current user
        applicationConfig.setSecurityContext(new UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities()));

        Map<String, List<String>> extraClaims = new HashMap<>();
        extraClaims.put("Roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );

        return ResponseCookie.from("access_token", jwtService.generateToken(user, extraClaims))
                .maxAge(900) // cookie expiration time in seconds
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
    }

    public AuthenticationResponseTokenCookie authenticate(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        var user = (UserDetails) authentication.getPrincipal();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseTokenCookie.builder().token(jwtToken).build();
    }
}
