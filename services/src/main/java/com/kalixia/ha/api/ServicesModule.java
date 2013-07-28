package com.kalixia.ha.api;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.SensorsDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.dao.cassandra.CassandraModule;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;

@Module(
        library = true,
        includes = {
                CassandraModule.class
        }
)
public class ServicesModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesModule.class);

    @Provides @Singleton UsersService provideUsersService(UsersDao dao) {
        try {
            UsersServiceImpl service = new UsersServiceImpl(dao);
            service.init();
            return service;
        } catch (IOException e) {
            LOGGER.error("Can' start users service...", e);
            throw new IllegalStateException("Can' start users service", e);
        }
    }

    @Provides @Singleton DevicesService provideDevicesService(DevicesDao dao) {
        return new DevicesServiceImpl(dao);
    }

    @Provides @Singleton SensorsService provideSensorsService(SensorsDao dao) {
        return new SensorsServiceImpl(dao);
    }

}
