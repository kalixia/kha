package com.kalixia.ha.api;

import com.kalixia.ha.model.User;

public interface UsersService {
    User findByUsername(String username);
    void saveUser(User user);
}
