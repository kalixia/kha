package com.kalixia.ha.api.cassandra

import com.kalixia.ha.api.UsersDao
import com.kalixia.ha.model.User
import com.netflix.astyanax.Keyspace

class UsersDaoTest extends AbstractCassandraDaoTest<UsersDao> {

    def "test storing and retrieving user"() {
        given:
        def user = new User(username: 'johndoe', email: 'john@doe.com', firstName: 'John', lastName: 'Doe')

        when:
        dao.save(user)

        then:
        user == dao.findByUsername(user.getUsername())
    }

    @Override
    protected UsersDao createDao(Keyspace keyspace) {
        return new CassandraUsersDao(keyspace)
    }

}
