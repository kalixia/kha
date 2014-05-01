package com.kalixia.ha.dao.lucene

import com.kalixia.ha.dao.AbstractDevicesDaoTest
import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.UsersDao

class DevicesDaoTest extends AbstractDevicesDaoTest {
    @Override
    UsersDao getUsersDao() {
        return LuceneDaoTests.usersDao
    }

    @Override
    DevicesDao getDevicesDao() {
        return LuceneDaoTests.devicesDao
    }

    @Override
    UUID createUUID() {
        return UUID.randomUUID()
    }

    def setupSpec() {
        LuceneDaoTests.indexWriter.deleteAll()
    }

}
