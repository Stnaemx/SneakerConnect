package com.Sneaker.SneakerConnect.entity.userCreation;

import com.Sneaker.SneakerConnect.entity.User;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.ConsignorCreationDto;
import com.Sneaker.SneakerConnect.service.CustomUserDetailsService;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsignorCreationStrategy implements UserCreationStrategy<ConsignorCreationDto> {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public User createUser(ConsignorCreationDto request) {
        List<Role> validateRoles = List.of(Role.EMPLOYEE, Role.OWNER, Role.USER);

        // throws exception if user already exist for any reason
        customUserDetailsService.validateUserForUserCreation(request.getEmail(), request.getShopName(), validateRoles);

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }
}
