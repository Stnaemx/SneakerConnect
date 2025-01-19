package com.Sneaker.SneakerConnect.entity;

import com.Sneaker.SneakerConnect.auth.dto.RegisterRequest;
import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import org.springframework.stereotype.Component;

@Component

public class UserShopFactory {

    public <T extends UserCreationDto> UsersShop createUserShop(T userCreationDto, User user, Shop shop, boolean isOwner) {
        return UsersShop.builder()
                .isOwner(isOwner)
                .userEmail(userCreationDto.getEmail())
                .user(user)
                .shop(shop)
                .build();
    }
}
