package com.kalixia.ha.model.security;

public interface Permissions {

    // permissions for users
    String USERS_ALL = "users:*";
    String USERS_CREATE = "users:create";
    String USERS_VIEW = "users:view";
    String USERS_COUNT = "users:count";

    // permissions for devices
    String DEVICES_ALL = "devices:*";
    String DEVICES_CREATE = "devices:create";
    String DEVICES_VIEW = "devices:view";

}
