package com.Sneaker.SneakerConnect.service;

import com.Sneaker.SneakerConnect.entity.User;
import com.Sneaker.SneakerConnect.entity.UsersShop;
import com.Sneaker.SneakerConnect.exceptions.UserAlreadyExistsException;
import com.Sneaker.SneakerConnect.repository.UserRepository;
import com.Sneaker.SneakerConnect.repository.UsersShopRepository;
import com.Sneaker.SneakerConnect.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UsersShopRepository usersShopRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    // permissives for creating each user types
    public void validateUserForUserCreation(String email, String shopName, List<Role> rolesToValidate) {
        Optional<UsersShop> conflictingUserShop = usersShopRepository.findByEmailAndRolesOrShop(
                email,
                rolesToValidate,
                shopName
        );

        if(conflictingUserShop.isPresent()) {
            throw new UserAlreadyExistsException(
                    String.format("User with email %s already exists as an %s or is registered for shop %s.",
                            email,
                            conflictingUserShop.get().getRole(),
                            shopName)
            );
        }
    }
}