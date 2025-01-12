package com.Sneaker.SneakerConnect.auth.controller;

import com.Sneaker.SneakerConnect.DtoValidator;
import com.Sneaker.SneakerConnect.auth.dto.AuthenticationRequest;
import com.Sneaker.SneakerConnect.auth.service.AuthenticationService;
import com.Sneaker.SneakerConnect.auth.dto.RegisterRequest;
import com.Sneaker.SneakerConnect.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final DtoValidator<RegisterRequest> registerRequestDtoValidator;
    private final DtoValidator<AuthenticationRequest> authenticationRequestDtoValidator;
    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<List<String>> register(@RequestBody RegisterRequest registerRequest) {
        // validate dto and return message to user if any fields are missing
        registerRequestDtoValidator.validate(registerRequest);

        return buildResponseWithCookies(authenticationService.register(registerRequest), registerRequest.getEmail());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<List<String>> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        authenticationRequestDtoValidator.validate(authenticationRequest);

        return buildResponseWithCookies(authenticationService.authenticate(authenticationRequest), authenticationRequest.getEmail());
    }

    // build http response with cookies. method receives a list of cookies to include
    private ResponseEntity<List<String>> buildResponseWithCookies(List<String> cookies, String userEmail) {
        HttpHeaders headers = new HttpHeaders();

        UserDetails user = customUserDetailsService.loadUserByUsername(userEmail);

        List<String> userRoles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // add each cookie individually
        cookies.forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie));

        return ResponseEntity.ok()
                .headers(headers)
                .body(userRoles);
    }
}
