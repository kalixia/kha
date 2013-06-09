package com.kalixia.ha.api;

import com.kalixia.ha.model.User;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import java.util.UUID;

public interface UsersDao {
    User findByUsername(String username) throws ConnectionException;
    void save(User user) throws ConnectionException;
}
