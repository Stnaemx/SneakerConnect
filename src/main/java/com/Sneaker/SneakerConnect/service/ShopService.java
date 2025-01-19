package com.Sneaker.SneakerConnect.service;

import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import com.Sneaker.SneakerConnect.entity.Shop;
import com.Sneaker.SneakerConnect.entity.ShopFactory;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.OwnerCreationDto;
import com.Sneaker.SneakerConnect.exceptions.ShopDoesNotExistException;
import com.Sneaker.SneakerConnect.repository.ShopRepository;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopFactory shopFactory;
    private final ShopRepository shopRepository;

    public <T extends UserCreationDto> Shop resolveShop(T registerRequest, Role role) {
        if(role == Role.OWNER && registerRequest instanceof OwnerCreationDto ownerRequest) {
            Shop shop = shopFactory.createShop(ownerRequest);
            shopRepository.save(shop);
            return shop;
        }

        return shopRepository.findByName(registerRequest.getShopName())
                .orElseThrow(() -> new ShopDoesNotExistException("Shop does not exist."));
    }
}
