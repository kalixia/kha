package com.kalixia.ha.api

import com.kalixia.ha.api.configuration.UsersServiceConfiguration
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User

class UsersServiceImpl extends Service<UsersServiceConfiguration> implements UsersService {
    final UsersDao dao

    UsersServiceImpl(UsersDao dao) {
        this.dao = dao;
    }

    @Override
    User findByUsername(String username) {
        LOGGER.info("Searching for user '{}'...", username)
        return dao.findByUsername(username)
    }

    @Override
    void saveUser(User user) {
        dao.save(user)
    }

    @Override
    rx.Observable<User> findUsers() {
        return dao.findUsers()
    }

    @Override
    Long getUsersCount() {
        return dao.getUsersCount()
    }

    @Override
    protected String getName() {
        return "users-service"
    }

    @Override
    protected Class<UsersServiceConfiguration> getConfigurationClass() {
        return UsersServiceConfiguration.class
    }
}
