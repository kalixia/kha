package com.kalixia.ha.dao.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.ExponentialReconnectionPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.kalixia.ha.dao.DevicesDao;
import com.kalixia.ha.dao.SensorsDao;
import com.kalixia.ha.dao.UsersDao;
import com.kalixia.ha.model.sensors.DataPoint;
import dagger.Module;
import dagger.Provides;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Module(library = true)
public class CassandraModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraModule.class);

    @Provides @Singleton UsersDao provideUserDao(Session session, SchemaCreator schemaCreator) {
        return new CassandraUsersDao(session);
    }

    @Provides @Singleton DevicesDao provideDevicesDao(Session session, UsersDao usersDao, SchemaCreator schemaCreator) {
        return new CassandraDevicesDao(session, usersDao);
    }

    @Singleton @Provides SensorsDao provideSensorsDao() {
        return new SensorsDao() {
            @Override
            public DataPoint getLastValue(UUID sensorID) {
                return null;
            }

            @Override
            public List<DataPoint> getHistory(DateTime from, DateTime to, Period period) {
                return null;
            }
        };
    }

    @Provides @Singleton Session provideSession(Cluster cluster) {
        try {
            return cluster.connect();
        } catch (Exception e) {
            LOGGER.error("Can't connect to Cassandra", e);
            return null;
        }
    }

    @Provides @Singleton Cluster provideCluster() {
        Cluster cluster = Cluster.builder()
                .addContactPoints("127.0.0.1")
                .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                .withReconnectionPolicy(new ExponentialReconnectionPolicy(100L, 5000L))
                .withLoadBalancingPolicy(new TokenAwarePolicy(new RoundRobinPolicy()))
                .build();
        Metadata metadata = cluster.getMetadata();
        LOGGER.info("Connected to cluster: '{}'", metadata.getClusterName());
        metadata.getAllHosts()
                .forEach(host -> LOGGER.info("Datacenter: '{}'; Host: '{}'; Rack: '{}'",
                        new Object[] { host.getDatacenter(), host.getAddress(), host.getRack() })
                );
        return cluster;
    }

}
