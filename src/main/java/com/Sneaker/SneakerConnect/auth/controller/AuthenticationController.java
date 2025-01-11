package com.Sneaker.SneakerConnect.auth.controller;

import com.Sneaker.SneakerConnect.auth.dto.AuthenticationRequest;
import com.Sneaker.SneakerConnect.auth.service.AuthenticationService;
import com.Sneaker.SneakerConnect.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            return buildResponseWithCookies(authenticationService.register(registerRequest));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Void> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return buildResponseWithCookies(authenticationService.authenticate(authenticationRequest));
    }

    // build http response with cookies. method receives a list of cookies to include
    private ResponseEntity<Void> buildResponseWithCookies(List<String> cookies) {
        HttpHeaders headers = new HttpHeaders();

        // add each cookie individually
        cookies.forEach(cookie -> headers.add(HttpHeaders.SET_COOKIE, cookie));

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }
}
