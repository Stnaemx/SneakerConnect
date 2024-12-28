package com.Sneaker.SneakerConnect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_CREATE("Admin: Create"),
    ADMIN_READ ("Admin: Read"),
    ADMIN_UPDATE("Admin: Update"),
    ADMIN_DELETE("Admin: Delete");

    @Getter
    private final String permission;
}
