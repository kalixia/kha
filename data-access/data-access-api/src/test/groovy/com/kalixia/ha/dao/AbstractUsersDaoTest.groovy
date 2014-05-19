package com.kalixia.ha.dao

import com.kalixia.ha.model.User
import spock.lang.Specification

abstract class AbstractUsersDaoTest extends Specification {

    def abstract UsersDao getUsersDao()

    def "test storing and retrieving user"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe')

        when:
        usersDao.save(user)

        then:
        user == usersDao.findByUsername(user.getUsername())
    }

}
