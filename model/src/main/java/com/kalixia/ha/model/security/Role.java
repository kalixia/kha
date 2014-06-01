package com.kalixia.ha.model.security;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum Role {
    ADMINISTRATOR(Permissions.USERS_ALL, Permissions.DEVICES_ALL),
    USER(Permissions.DEVICES_ALL),
    ANONYMOUS;

    private final Set<String> permissions;

    Role(String... permissions) {
        this.permissions = ImmutableSet.<String>builder()
                .add(permissions)
                .build();
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}
