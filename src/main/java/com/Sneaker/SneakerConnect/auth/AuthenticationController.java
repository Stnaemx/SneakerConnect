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
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return buildResponseWithCookies(authenticationService.register(registerRequest));
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
