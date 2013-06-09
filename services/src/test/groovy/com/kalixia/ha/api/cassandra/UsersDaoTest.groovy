package com.kalixia.ha.api.cassandra

import com.kalixia.ha.model.User

class UsersDaoTest extends AbstractCassandraDaoTest {

    def "test storing and retrieving user"() {
        given:
        def user = new User(username: 'johndoe', email: 'john@doe.com', firstName: 'John', lastName: 'Doe')

        when:
        usersDao.save(user)

        then:
        user == usersDao.findByUsername(user.getUsername())
    }

}
