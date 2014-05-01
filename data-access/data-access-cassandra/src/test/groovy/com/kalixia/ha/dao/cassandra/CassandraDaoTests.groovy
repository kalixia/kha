package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.SensorsDao
import com.kalixia.ha.dao.UsersDao
import dagger.ObjectGraph

class CassandraDaoTests {
    public final static UsersDao usersDao
    public final static DevicesDao devicesDao
    public final static SensorsDao sensorsDao

    static {
        ObjectGraph objectGraph = ObjectGraph.create(new TestModule());
        DataHolder holder = objectGraph.get(DataHolder.class)

        usersDao = holder.usersDao
        devicesDao = holder.devicesDao
        sensorsDao = holder.sensorsDao
    }
}
