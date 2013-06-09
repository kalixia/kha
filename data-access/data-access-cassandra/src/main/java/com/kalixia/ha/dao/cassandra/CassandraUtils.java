package com.kalixia.ha.dao.cassandra;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraUtils.class);

    public static KeyspaceDefinition getOrCreateKeyspace(Keyspace keyspace) throws ConnectionException {
        KeyspaceDefinition keyspaceDefinition;
        try {
            keyspaceDefinition = keyspace.describeKeyspace();
        } catch (ConnectionException e) {
            // create keyspace if missing
            LOGGER.warn("Keyspace '{}' does not exists. Creation in progress...", keyspace.getKeyspaceName());
            keyspace.createKeyspace(ImmutableMap.<String, Object>builder()
                    .put("strategy_options", ImmutableMap.<String, Object>builder()
                            .put("replication_factor", "2")
                            .build())
                    .put("strategy_class", "SimpleStrategy")
                    .build()
            );
            keyspaceDefinition = keyspace.describeKeyspace();
        }
        return keyspaceDefinition;
    }

}
