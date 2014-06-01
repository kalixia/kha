package com.kalixia.ha.dao

import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.User
import spock.lang.Specification

import static java.util.Collections.emptySet

abstract class AbstractUsersDaoTest extends Specification {

    def abstract UsersDao getUsersDao()

    def "test storing and retrieving user"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe',
                [Role.ADMINISTRATOR, Role.USER] as Set<Role>,
                emptySet()      // Oauth2 access tokens of the user
        )

        when:
        usersDao.save(user)

        then:
        user == usersDao.findByUsername(user.getUsername())
    }

}
