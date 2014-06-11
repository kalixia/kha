package com.kalixia.ha.dao.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

@Module(injects = DataHolder.class, includes = CassandraModule.class, overrides = true)
public class TestModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestModule.class);

    @Provides @Singleton Cluster provideCluster() {
        try {
            Cluster cluster = Cluster.builder()
                    .addContactPointsWithPorts(Arrays.asList(
//                            new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9042) // mvn cassandra:run + nodetool enablebinary
                            new InetSocketAddress(InetAddress.getByName("localhost"), 9142)  // cassandraunit
                    ))
                    .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                    .withReconnectionPolicy(new ExponentialReconnectionPolicy(100L, 5000L))
                    .build();
            Metadata metadata = cluster.getMetadata();
            LOGGER.info("Connected to cluster: '{}'", metadata.getClusterName());
            metadata.getAllHosts()
                    .forEach(host -> LOGGER.info("Datacenter: '{}'; Host: '{}'; Rack: '{}'",
                            new Object[] { host.getDatacenter(), host.getAddress(), host.getRack() })
                    );
            return cluster;
        } catch (UnknownHostException e) {
            LOGGER.error("Can't connect to Cassandra", e);
            return null;
        }
    }


}
