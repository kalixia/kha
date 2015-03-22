package com.kalixia.ha.dao

import com.kalixia.ha.model.User
import com.kalixia.ha.model.security.OAuthTokens
import com.kalixia.ha.model.security.Role
import spock.lang.Specification

abstract class AbstractUsersDaoTest extends Specification {

    def abstract UsersDao getUsersDao()

    def "test storing and retrieving user"() {
        when:
        String accessToken1 = UUID.randomUUID().toString()
        String accessToken2 = UUID.randomUUID().toString()
        String accessToken3 = UUID.randomUUID().toString()
        def oauthTokens = [
                new OAuthTokens(accessToken1, UUID.randomUUID().toString()),
                new OAuthTokens(accessToken2, UUID.randomUUID().toString()),
                new OAuthTokens(accessToken3, UUID.randomUUID().toString())
        ] as Set<OAuthTokens>
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe',
                [Role.ADMINISTRATOR, Role.USER] as Set<Role>, oauthTokens)

        then:
        usersDao.getUsersCount() == 0

        when:
        Optional<User> found = usersDao.findByUsername('foo')

        then:
        !found.isPresent()

        when:
        found = usersDao.findByOAuthAccessToken(UUID.randomUUID().toString())

        then:
        !found.isPresent()

        when:
        def users = usersDao.findUsers().toList().toBlocking().single()

        then:
        users != null
        users.size() == 0

        when:
        usersDao.save(user)
        found = usersDao.findByUsername(user.getUsername())
        users = usersDao.findUsers().toList().toBlocking().single();

        then:
        usersDao.getUsersCount() == 1
        found.isPresent()
        user == found.get()
        users != null
        users.size() == 1
        user == users.get(0)

        when:
        found = usersDao.findByOAuthAccessToken(accessToken1)

        then:
        found.isPresent()
        user == found.get()
    }

}
