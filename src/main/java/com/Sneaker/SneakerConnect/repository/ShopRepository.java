package com.Sneaker.SneakerConnect.repository;

import com.Sneaker.SneakerConnect.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByNameOrAddress(String name, String address);
    Optional<Shop> findByName(String name);
    boolean existsByName(String name);
}
