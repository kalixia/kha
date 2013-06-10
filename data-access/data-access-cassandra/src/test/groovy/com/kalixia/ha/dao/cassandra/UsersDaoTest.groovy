package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.model.User

class UsersDaoTest extends AbstractCassandraDaoTest {

    def "test storing and retrieving user"() {
        given:
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')

        when:
        usersDao.save(user)

        then:
        user == usersDao.findByUsername(user.getUsername())
    }

}
