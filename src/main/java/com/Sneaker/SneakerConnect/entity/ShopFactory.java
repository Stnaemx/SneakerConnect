package com.Sneaker.SneakerConnect.entity;

import com.Sneaker.SneakerConnect.entity.userCreation.dto.OwnerCreationDto;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class ShopFactory {

    public Shop createShop(OwnerCreationDto ownerCreationDto) {
        return Shop.builder()
                    .name(ownerCreationDto.getShopName())
                    .address(ownerCreationDto.getAddress())
                    .timestamp(LocalDateTime.now())
                    .build();
    }
}