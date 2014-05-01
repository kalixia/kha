package com.kalixia.ha.dao.cassandra;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.SensorsDao;
import com.kalixia.ha.dao.UsersDao;

import javax.inject.Inject;

public class DataHolder {
    private final UsersDao usersDao;
    private final DevicesDao devicesDao;
    private final SensorsDao sensorsDao;

    @Inject
    public DataHolder(UsersDao usersDao, DevicesDao devicesDao, SensorsDao sensorsDao) {
        this.usersDao = usersDao;
        this.devicesDao = devicesDao;
        this.sensorsDao = sensorsDao;
    }

    public UsersDao getUsersDao() {
        return usersDao;
    }

    public DevicesDao getDevicesDao() {
        return devicesDao;
    }

    public SensorsDao getSensorsDao() {
        return sensorsDao;
    }
}
