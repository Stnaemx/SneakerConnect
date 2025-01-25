package com.Sneaker.SneakerConnect.entity;

import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import com.Sneaker.SneakerConnect.user.Role;
import org.springframework.stereotype.Component;

@Component

public class UserShopFactory {

    // generic declaration indicating meaning this method can work with any subtype of UserCreationDto
    public <T extends UserCreationDto> UsersShop createUserShop(T userCreationDto, User user, Shop shop, boolean isOwner, Role role) {
        return UsersShop.builder()
                .isOwner(isOwner)
                .userEmail(userCreationDto.getEmail())
                .user(user)
                .shop(shop)
                .role(role)
                .build();
    }
}
