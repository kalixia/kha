package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.SensorsDao
import com.kalixia.ha.dao.UsersDao
import com.netflix.astyanax.Keyspace
import groovy.util.logging.Slf4j
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import spock.lang.Shared
import spock.lang.Specification

/**
 * Abstract class easing tests for Cassandra DAOs.
 */
@Slf4j("LOGGER")
abstract class AbstractCassandraDaoTest extends Specification {
    @Shared UsersDao usersDao
    @Shared SensorsDao sensorsDao
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
        sensorsDao = new CassandraSensorsDao(keyspace)
        devicesDao = new CassandraDevicesDao(keyspace, usersDao)
    }

    def cleanupSpec() {
        LOGGER.info("Stopping Embedded Cassandra Server...")
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra()
    }

}
