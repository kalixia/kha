package com.kalixia.ha.api.cassandra;

import com.kalixia.ha.api.SensorsDao;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolType;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module//(entryPoints = SensorsDao.class)
public class CassandraModule {

//    @Provides SensorsDao buildSensorsDao(Keyspace keyspace) {
//        return new CassandraSensorsDao<>(keyspace);
//    }

    @Provides @Singleton Keyspace buildKeyspace(AstyanaxContext<Keyspace> ctx) {
        ctx.start();
        return ctx.getEntity();
    }

    @Provides @Singleton AstyanaxContext<Keyspace> buildContext(ConnectionPoolConfiguration pool) {
        AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
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
        return context;
    }

    @Provides @Singleton ConnectionPoolConfiguration buildConnectionPool() {
        ConnectionPoolConfigurationImpl pool = new ConnectionPoolConfigurationImpl("MyConnectionPool")
                .setPort(9160)
                .setMaxConnsPerHost(3)
                .setSeeds("127.0.0.1:9160");
        return pool;
    }

}
