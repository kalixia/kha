package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.AbstractDevicesDaoTest
import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.UsersDao

class DevicesDaoTest extends AbstractDevicesDaoTest {

    @Override
    UsersDao getUsersDao() {
        return CassandraDaoTests.usersDao
    }

    @Override
    DevicesDao getDevicesDao() {
        return CassandraDaoTests.devicesDao
    }

    @Override
    UUID createUUID() {
        return UUID.randomUUID()
    }

    def setupSpec() {
        EmbeddedCassandraUtils.setupRepository()
    }

    def cleanupSpec() {
        EmbeddedCassandraUtils.cleanupRepository()
    }

}
