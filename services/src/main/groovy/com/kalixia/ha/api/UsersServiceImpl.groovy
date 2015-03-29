package com.kalixia.ha.api

import com.kalixia.ha.api.configuration.UsersServiceConfiguration
import com.kalixia.ha.dao.UsersDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.security.OAuthTokens
import org.apache.shiro.authc.credential.PasswordService

import static com.google.common.base.Preconditions.checkNotNull

class UsersServiceImpl extends Service<UsersServiceConfiguration> implements UsersService {
    final UsersDao dao
    final PasswordService passwordService

    UsersServiceImpl(UsersDao dao, PasswordService passwordService) {
        checkNotNull(dao, "Users DAO can't be null")
        checkNotNull(passwordService, "Password Service can't be null")
        this.dao = dao
        this.passwordService = passwordService
    }

    @Override
    Optional<User> findByUsername(String username) {
        LOGGER.info("Searching for user '{}'...", username)
        return dao.findByUsername(username)
    }

    @Override
    User createUser(User user) {
        String hash = passwordService.encryptPassword(user.getPassword())
        user.setPassword(hash)
        user.addOAuthAccessToken(new OAuthTokens())
        saveUser(user)
        return user
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
    long getUsersCount() {
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
