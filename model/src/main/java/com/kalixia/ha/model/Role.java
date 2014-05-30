package com.kalixia.ha.model;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public enum Role {
    ADMINISTRATOR("users:*"), USER, ANONYMOUS;

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
