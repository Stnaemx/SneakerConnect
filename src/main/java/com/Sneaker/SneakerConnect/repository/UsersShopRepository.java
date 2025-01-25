package com.Sneaker.SneakerConnect.repository;

import com.Sneaker.SneakerConnect.entity.UsersShop;
import com.Sneaker.SneakerConnect.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersShopRepository extends JpaRepository<UsersShop, Long> {

    Optional<UsersShop> findByUserEmail(String userEmail);
    Optional<UsersShop> findByUserEmailAndShopName(String userEmail, String ShopName);

    // check if Role:Owner or Role:Employee already exist in system and is current user(Consignor) is registered for existing shop
    @Query("SELECT u FROM usershop u WHERE u.userEmail = :userEmail AND (u.role IN (:roles) OR u.shop.name = :shopName)")
    Optional<UsersShop> findByEmailAndRolesOrShop(@Param("userEmail") String userEmail,
                                                  @Param("roles") List<Role> roles,
                                                  @Param("shopName") String shopName);
}
