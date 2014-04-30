package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.AbstractUsersDaoTest
import com.kalixia.ha.dao.UsersDao

class UsersDaoTest extends AbstractUsersDaoTest {
    @Override
    UsersDao getUsersDao() {
        return CassandraDaoTests.usersDao
    }

    def setupSpec() {
        EmbeddedCassandraUtils.setupRepository()
    }

    def cleanupSpec() {
        EmbeddedCassandraUtils.cleanupRepository()
    }
}
