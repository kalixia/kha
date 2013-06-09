package com.kalixia.ha.dao;

import com.kalixia.ha.model.User;

public interface UsersDao {
    User findByUsername(String username) throws Exception;
    void save(User user) throws Exception;
}
