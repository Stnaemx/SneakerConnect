package com.Sneaker.SneakerConnect.entity.userCreation;

import com.Sneaker.SneakerConnect.entity.User;
import com.Sneaker.SneakerConnect.entity.UsersShop;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.ConsignorCreationDto;
import com.Sneaker.SneakerConnect.exceptions.UserAlreadyExistsException;
import com.Sneaker.SneakerConnect.repository.UsersShopRepository;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsignorCreationStrategy implements UserCreationStrategy<ConsignorCreationDto> {

    private final PasswordEncoder passwordEncoder;
    private final UsersShopRepository usersShopRepository;

    @Override
    public User createUser(ConsignorCreationDto request) {
        // TODO: determine which shop a user is registering for

        // check if user exist for a shop
        Optional<UsersShop> existingUsersShop = usersShopRepository.findByUserEmail(request.getEmail());

        if(existingUsersShop.isPresent()) {
            throw new UserAlreadyExistsException("User exists. Please choose a different name.");
        }

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
        return Role.EMPLOYEE;
    }
}
