package com.Sneaker.SneakerConnect.repository;

import com.Sneaker.SneakerConnect.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByName(String name);

    Optional<Shop> findByAddress(String address);
}
