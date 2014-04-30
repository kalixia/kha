package com.kalixia.ha.dao.lucene

import com.kalixia.ha.model.User

class UsersDaoTest extends AbstractLuceneDaoTest {

    def "test storing and retrieving user"() {
        given:
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')

        when:
        usersDao.save(user)

        then:
        user == usersDao.findByUsername(user.getUsername())
    }

}
