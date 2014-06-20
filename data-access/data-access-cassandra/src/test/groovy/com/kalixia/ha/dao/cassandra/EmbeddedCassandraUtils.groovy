package com.kalixia.ha.dao.cassandra

import groovy.util.logging.Slf4j
import org.cassandraunit.utils.EmbeddedCassandraServerHelper

@Slf4j("LOGGER")
class EmbeddedCassandraUtils {
    def static setupRepository() {
        LOGGER.info("Starting Embedded Cassandra Server...")
        EmbeddedCassandraServerHelper.startEmbeddedCassandra("/cassandra.yaml")
    }

    def static cleanupRepository() {
        LOGGER.info("Stopping Embedded Cassandra Server...")
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra()
    }
}
