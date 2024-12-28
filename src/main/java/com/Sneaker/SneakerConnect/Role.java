package com.Sneaker.SneakerConnect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.Sneaker.SneakerConnect.Permission.*;

@RequiredArgsConstructor
public enum Role {

    OWNER(Set.of(
            ADMIN_CREATE, ADMIN_READ, ADMIN_UPDATE, ADMIN_DELETE
        )),
    EMPLOYEE(Collections.emptySet()),
    USER(Collections.emptySet());

    // Permission require storage in order to be associated with Role constants
    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
