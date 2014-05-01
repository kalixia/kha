package com.kalixia.ha.dao.cassandra;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.ConnectionPoolConfiguration;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;
import dagger.Module;
import dagger.Provides;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

import static com.netflix.astyanax.model.ConsistencyLevel.CL_ONE;

@Module(injects = DataHolder.class, includes = CassandraModule.class, overrides = true)
public class TestModule {

    @Provides @Singleton AstyanaxContext<Keyspace> provideContext(ConnectionPoolConfiguration pool) {
        LoggerFactory.getLogger(getClass()).info("Using test configuration...");
        return new AstyanaxContext.Builder()
                .forCluster("MyCluster")
                .forKeyspace("test")
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
                                //                        .setDiscoveryType(RING_DESCRIBE)
                                //                        .setConnectionPoolType(TOKEN_AWARE)
                                .setDefaultReadConsistencyLevel(CL_ONE)
                                .setDefaultWriteConsistencyLevel(CL_ONE)
                                .setTargetCassandraVersion("1.2")
                                .setCqlVersion("3.0.0")
                )
                .withConnectionPoolConfiguration(pool)
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());
    }

}
