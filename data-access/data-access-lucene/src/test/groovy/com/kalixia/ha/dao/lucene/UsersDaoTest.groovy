package com.kalixia.ha.dao.lucene

import com.kalixia.ha.dao.AbstractUsersDaoTest
import com.kalixia.ha.dao.UsersDao

class UsersDaoTest extends AbstractUsersDaoTest {
    @Override
    UsersDao getUsersDao() {
        return LuceneDaoTests.usersDao
    }

    def setupSpec() {
        LuceneDaoTests.indexWriter.deleteAll()
    }

}
