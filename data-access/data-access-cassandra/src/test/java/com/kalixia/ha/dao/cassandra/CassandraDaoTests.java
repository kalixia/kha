package com.kalixia.ha.dao.cassandra;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.UsersDao;
import dagger.ObjectGraph;

public class CassandraDaoTests {
    public final static UsersDao usersDao;
    public final static DevicesDao devicesDao;
//    public final static SensorsDao sensorsDao;
    public final static SchemaCreator schemaCreator;

    static {
        ObjectGraph objectGraph = ObjectGraph.create(new TestModule());
        DataHolder holder = objectGraph.get(DataHolder.class);

        usersDao = holder.getUsersDao();
        devicesDao = holder.getDevicesDao();
//        sensorsDao = holder.sensorsDao;
        schemaCreator = holder.getSchemaCreator();
    }
}
