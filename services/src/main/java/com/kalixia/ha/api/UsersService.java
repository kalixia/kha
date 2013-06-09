package com.kalixia.ha.api;

import com.kalixia.ha.model.User;
import java.util.UUID;

public interface UsersService {
    User findById(UUID id);
    User findByUsername(String username);
    void saveUser(User user);
}
