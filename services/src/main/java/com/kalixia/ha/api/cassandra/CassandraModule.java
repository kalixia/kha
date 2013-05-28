package com.kalixia.ha.api.cassandra;

import com.kalixia.ha.api.DevicesDao;
import com.kalixia.ha.api.SensorsDao;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Module(library = true)
public class CassandraModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraModule.class);

    @Provides @Singleton DevicesDao provideDevicesDao(Keyspace keyspace) {
        try {
            return new CassandraDevicesDao<>(keyspace);
        } catch (ConnectionException e) {
            LOGGER.error("Can't initialize Devices DAO", e);
            return null;
        }
    }

    @Provides @Singleton SensorsDao provideSensorsDao(Keyspace keyspace) {
        try {
            return new CassandraSensorsDao<>(keyspace);
        } catch (ConnectionException e) {
            LOGGER.error("Can't initialize Sensors DAO", e);
            return null;
        }
    }

    @Provides @Singleton Keyspace provideKeyspace(AstyanaxContext<Keyspace> ctx) {
        ctx.start();
        return ctx.getClient();
    }

    @Provides @Singleton AstyanaxContext<Keyspace> provideContext(ConnectionPoolConfiguration pool) {
        return new AstyanaxContext.Builder()
                .forCluster("MyCluster")
                .forKeyspace("test")
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                        .setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
                        .setConnectionPoolType(ConnectionPoolType.TOKEN_AWARE)
                )
                .withConnectionPoolConfiguration(pool
//                        .setBadHostDetector(new BadHostDetectorImpl(pool))
//                        .setRetryBackoffStrategy(new ExponentialRetryBackoffStrategy(pool))
                )
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());
    }

    @Provides @Singleton ConnectionPoolConfiguration provideConnectionPool() {
        return new ConnectionPoolConfigurationImpl("MyConnectionPool")
                .setPort(9160)
                .setMaxConnsPerHost(3)
                .setSeeds("127.0.0.1:9160");
    }

}
