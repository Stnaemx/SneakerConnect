package com.Sneaker.SneakerConnect.auth;

import com.Sneaker.SneakerConnect.Role;
import com.Sneaker.SneakerConnect.UserRepository;
import com.Sneaker.SneakerConnect.config.ApplicationConfig;
import com.Sneaker.SneakerConnect.config.JwtService;
import com.Sneaker.SneakerConnect.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ApplicationConfig applicationConfig;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseToken register(RegisterRequest registerRequest) {
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.OWNER)
                .build();
        userRepository.save(user);
        // set the security context for the current user
        applicationConfig.setSecurityContext(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseToken.builder().token(jwtToken).build();
    }

    public AuthenticationResponseToken authenticate(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        var user = (UserDetails) authentication.getPrincipal();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseToken.builder().token(jwtToken).build();
    }

}
