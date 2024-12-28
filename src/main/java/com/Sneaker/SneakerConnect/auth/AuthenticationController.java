package com.Sneaker.SneakerConnect.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<AuthenticationResponseToken> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthenticationResponseToken responseToken = authenticationService.register(registerRequest);
        return ResponseEntity.ok(responseToken);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseToken> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponseToken responseToken = authenticationService.authenticate(authenticationRequest);
        return ResponseEntity.ok(responseToken);
    }
}
