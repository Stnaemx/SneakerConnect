package com.Sneaker.SneakerConnect.auth.controller;

import com.Sneaker.SneakerConnect.DtoValidator;
import com.Sneaker.SneakerConnect.auth.dto.AuthenticationRequest;
import com.Sneaker.SneakerConnect.auth.service.AuthenticationService;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.ConsignorCreationDto;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.OwnerCreationDto;
import com.Sneaker.SneakerConnect.service.CustomUserDetailsService;
import com.Sneaker.SneakerConnect.service.ShopService;
import com.Sneaker.SneakerConnect.user.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthenticationController {

    private final DtoValidator<OwnerCreationDto> ownerCreationDtoDtoValidator;
    private final DtoValidator<ConsignorCreationDto> consignorCreationDtoDtoValidator;
    private final DtoValidator<AuthenticationRequest> authenticationRequestDtoValidator;
    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ShopService shopService;

    @PostMapping("/register")
    // returns a list of roles
    public ResponseEntity<List<String>> registerOwner(@RequestBody OwnerCreationDto ownerCreationDto) {
        // validate dto and return message to user if any fields are missing
        ownerCreationDtoDtoValidator.validate(ownerCreationDto);

        return buildResponseWithCookies(authenticationService.register(ownerCreationDto, Role.OWNER), ownerCreationDto.getEmail());
    }

    @PostMapping("/register/user")
    public ResponseEntity<List<String>> registerUser(@RequestBody ConsignorCreationDto consignorCreationDto) {
        // validate dto and return message to user if any fields are missing
        consignorCreationDtoDtoValidator.validate(consignorCreationDto);

        // check if shop exists
        shopService.shopExist(consignorCreationDto.getShopName());
        return buildResponseWithCookies(authenticationService.register(consignorCreationDto, Role.USER), consignorCreationDto.getEmail());
    }

    @GetMapping("/{shopName}")
    public ResponseEntity<Void> validateShop(@PathVariable String shopName) {
        shopService.shopExist(shopName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<List<String>> authenticate(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        authenticationRequestDtoValidator.validate(authenticationRequest);

        return buildResponseWithCookies(authenticationService.authenticate(authenticationRequest), authenticationRequest.getEmail());
    }

    // build http response with cookies. method receives a list of cookies to include
    // response body contains a list of user roles
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
