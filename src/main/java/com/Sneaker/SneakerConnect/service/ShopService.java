package com.Sneaker.SneakerConnect.service;

import com.Sneaker.SneakerConnect.auth.dto.UserCreationDto;
import com.Sneaker.SneakerConnect.entity.Shop;
import com.Sneaker.SneakerConnect.entity.ShopFactory;
import com.Sneaker.SneakerConnect.entity.userCreation.dto.OwnerCreationDto;
import com.Sneaker.SneakerConnect.exceptions.ShopDoesNotExistException;
import com.Sneaker.SneakerConnect.repository.ShopRepository;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopFactory shopFactory;
    private final ShopRepository shopRepository;

    // either registers the shop (for owners) or returns an existing shop
    public <T extends UserCreationDto> Shop resolveShop(T registerRequest, Role role) {
        if(role == Role.OWNER && registerRequest instanceof OwnerCreationDto ownerRequest) {
            Shop shop = shopFactory.createShop(ownerRequest);
            shopRepository.save(shop);
            return shop;
        }

        return shopRepository.findByName(registerRequest.getShopName())
                .orElseThrow(() -> new ShopDoesNotExistException("Shop does not exist."));
    }

    public void shopExist(String shopName) {
        if(!shopRepository.existsByName(shopName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
