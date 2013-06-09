package com.kalixia.ha.api.cassandra

import com.kalixia.ha.api.DevicesDao
import com.kalixia.ha.api.UsersDao
import com.netflix.astyanax.Keyspace
import groovy.util.logging.Slf4j
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import spock.lang.Shared

/**
 * Abstract class easing tests for Cassandra DAOs.
 */
@Slf4j("LOGGER")
abstract class AbstractCassandraDaoTest extends spock.lang.Specification {
    @Shared UsersDao usersDao
    @Shared DevicesDao devicesDao
    @Shared Keyspace keyspace;

    def setupSpec() {
        LOGGER.info("Starting Embedded Cassandra Server...")
        EmbeddedCassandraServerHelper.startEmbeddedCassandra()

        CassandraModule cassandraModule = new CassandraModule()
        def cassandraPool = cassandraModule.provideConnectionPool()
        def cassandraContext = cassandraModule.provideContext(cassandraPool)
        keyspace = cassandraModule.provideKeyspace(cassandraContext)

        usersDao = new CassandraUsersDao(keyspace)
        devicesDao = new CassandraDevicesDao(keyspace, usersDao)
    }

    def cleanupSpec() {
        LOGGER.info("Stopping Embedded Cassandra Server...")
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra()
    }

}
