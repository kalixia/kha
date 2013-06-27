package com.kalixia.ha.dao.cassandra;

import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.SensorsDao;
import com.kalixia.ha.dao.UsersDao;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.impl.BadHostDetectorImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.connectionpool.impl.ExponentialRetryBackoffStrategy;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.retry.BoundedExponentialBackoff;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Singleton;

import static com.netflix.astyanax.connectionpool.NodeDiscoveryType.RING_DESCRIBE;
import static com.netflix.astyanax.connectionpool.impl.ConnectionPoolType.TOKEN_AWARE;
import static com.netflix.astyanax.model.ConsistencyLevel.CL_LOCAL_QUORUM;
import static com.netflix.astyanax.model.ConsistencyLevel.CL_QUORUM;

@Module(library = true)
public class CassandraModule {

    @Provides @Singleton UsersDao provideUserDao(SchemaDefinition schema) {
        return new CassandraUsersDao(schema);
    }

    @Provides @Singleton DevicesDao provideDevicesDao(SchemaDefinition schema, UsersDao usersDao) {
        return new CassandraDevicesDao(schema, usersDao);
    }

    @Provides @Singleton SensorsDao provideSensorsDao(Keyspace keyspace) {
//        try {
            return new CassandraSensorsDao(keyspace);
//        } catch (ConnectionException e) {
//            LOGGER.error("Can't initialize Sensors DAO", e);
//            return null;
//        }
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
                        .setDiscoveryType(RING_DESCRIBE)
                        .setConnectionPoolType(TOKEN_AWARE)
//                        .setDefaultReadConsistencyLevel(CL_QUORUM)
//                        .setDefaultWriteConsistencyLevel(CL_QUORUM)
                        .setTargetCassandraVersion("1.2")
                        .setCqlVersion("3.0.0")
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
                .setSeeds("127.0.0.1,127.0.0.1:9171,10.33.2.11,10.33.2.12");
    }

}
