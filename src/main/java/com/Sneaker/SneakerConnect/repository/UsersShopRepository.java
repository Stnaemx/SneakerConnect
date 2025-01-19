package com.Sneaker.SneakerConnect.repository;

import com.Sneaker.SneakerConnect.entity.UsersShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersShopRepository extends JpaRepository<UsersShop, Long> {

    Optional<UsersShop> findByUserEmail(String userEmail);
}
