package com.Sneaker.SneakerConnect.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        HttpHeaders headers = new HttpHeaders();
        // add cookie to header
        headers.add(HttpHeaders.SET_COOKIE, authenticationService.register(registerRequest).toString());

        return ResponseEntity.ok()
                .headers(headers)
                .build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseTokenCookie> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponseTokenCookie responseToken = authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(responseToken);
    }
}
