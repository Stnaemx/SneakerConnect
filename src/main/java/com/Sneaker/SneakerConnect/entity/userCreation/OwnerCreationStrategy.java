package com.Sneaker.SneakerConnect.entity.userCreation;

import com.Sneaker.SneakerConnect.entity.Shop;
import com.Sneaker.SneakerConnect.entity.User;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.OwnerCreationDto;
import com.Sneaker.SneakerConnect.exceptions.UserAlreadyExistsException;
import com.Sneaker.SneakerConnect.repository.ShopRepository;
import com.Sneaker.SneakerConnect.repository.UserRepository;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OwnerCreationStrategy implements UserCreationStrategy<OwnerCreationDto> {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;


    @Override
    public User createUser(OwnerCreationDto request) {

        Optional<Shop> existingShop = shopRepository.findByNameOrAddress(request.getShopName(), request.getAddress());
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        // franchise creation cannot be done during sign up
        // exception will be handled by global exception controller handler
        if(existingShop.isPresent() || existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User or shop already exists. Please choose a different name.");
        }

        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.OWNER)
                .build();
    }

    @Override
    public Role getRole() {
        return Role.OWNER;
    }
}
