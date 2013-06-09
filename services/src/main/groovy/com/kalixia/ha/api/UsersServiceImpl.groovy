package com.kalixia.ha.api

import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import groovy.util.logging.Slf4j

@Slf4j(value = "LOGGER")
class UsersServiceImpl implements UsersService {
    final UsersDao dao

    UsersServiceImpl(UsersDao dao) {
        this.dao = dao;
    }

    @Override
    User findByUsername(String username) {
        return dao.findByUsername(username)
    }

    @Override
    void saveUser(User user) {
        dao.save(user)
    }
}
