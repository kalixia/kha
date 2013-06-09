package com.kalixia.ha.api.cassandra

import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.test.EmbeddedCassandra
import spock.lang.Shared
/**
 * Abstract class easing tests for Cassandra DAOs.
 */
abstract class AbstractCassandraDaoTest<T> extends spock.lang.Specification {
    @Shared T dao
    @Shared EmbeddedCassandra cassandra

    def setupSpec() {
        cassandra = new EmbeddedCassandra()
        cassandra.start()

        CassandraModule cassandraModule = new CassandraModule()
        def cassandraPool = cassandraModule.provideConnectionPool()
        def cassandraContext = cassandraModule.provideContext(cassandraPool)
        def keyspace = cassandraModule.provideKeyspace(cassandraContext)

        dao = createDao(keyspace)
    }

    def cleanupSpec() {
        cassandra.stop()
    }

    protected abstract T createDao(Keyspace keyspace);
}
