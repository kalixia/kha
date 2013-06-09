package com.kalixia.ha.api;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.SensorsDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.dao.cassandra.CassandraModule;
import com.kalixia.ha.dao.cassandra.DeviceRK;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        library = true,
        includes = {
                CassandraModule.class
        }
)
public class ServicesModule {

    @Provides @Singleton UsersService provideUsersService(UsersDao dao) {
        return new UsersServiceImpl(dao);
    }

    @Provides @Singleton DevicesService<DeviceRK> provideDevicesService(DevicesDao dao) {
        return new DevicesServiceImpl(dao);
    }

    @Provides @Singleton SensorsService provideSensorsService(SensorsDao dao) {
        return new SensorsServiceImpl(dao);
    }

}
