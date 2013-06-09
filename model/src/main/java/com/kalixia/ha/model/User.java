package com.kalixia.ha.model;

import java.util.UUID;

public class User {
    private UUID id;
    private final String username;

    public User(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
