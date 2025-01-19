package com.Sneaker.SneakerConnect.entity.userCreation;

import com.Sneaker.SneakerConnect.entity.User;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.EmployeeCreationDto;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeCreationStrategy implements UserCreationStrategy<EmployeeCreationDto> {

    private final PasswordEncoder passwordEncoder;

    // TODO: implement email

    @Override
    public User createUser(EmployeeCreationDto request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .build();
    }

    @Override
    public Role getRole() {
        return Role.USER;
    }
}
