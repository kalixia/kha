package com.kalixia.ha.dao.cassandra;

import com.google.common.collect.ImmutableMap;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

import static com.netflix.astyanax.serializers.ComparatorType.UTF8TYPE;
import static com.netflix.astyanax.serializers.ComparatorType.UUIDTYPE;

@Singleton
public class SchemaDefinition {
    private final Keyspace keyspace;
    private final ColumnFamily<String, UserProperty> cfUsers;
    private final ColumnFamily<UUID, SensorProperty> cfDevices;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaDefinition.class);

    @Inject
    public SchemaDefinition(Keyspace keyspace) {
        this.keyspace = keyspace;

        // prepare Users CF
        AnnotatedCompositeSerializer<UserProperty> userPropertySerializer =
                new AnnotatedCompositeSerializer<>(UserProperty.class);
        cfUsers = new ColumnFamily<>("Users", StringSerializer.get(), userPropertySerializer);
        // prepare Devices CF
        AnnotatedCompositeSerializer<SensorProperty> sensorPropertySerializer =
                new AnnotatedCompositeSerializer<>(SensorProperty.class);
        cfDevices = new ColumnFamily<>("Devices", UUIDSerializer.get(), sensorPropertySerializer);

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition;
        try {
            keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);
        } catch (ConnectionException e) {
            LOGGER.error("Can't get or create keyspace", e);
            return;
        }

        // create Users CF if missing
        if (keyspaceDefinition.getColumnFamily(cfUsers.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfUsers.getName());
            try {
                keyspace.createColumnFamily(cfUsers, ImmutableMap.<String, Object>builder()
                        .put("key_validation_class", UTF8TYPE.getTypeName())
                        .put("comparator_type", "CompositeType(UTF8Type,UTF8Type)")
                        .build());
            } catch (ConnectionException e) {
                LOGGER.error(
                        String.format("Can't create '%s' column family", cfUsers.getName()),
                        e);
                return;
            }
        }
        // create Devices CF if missing
        if (keyspaceDefinition.getColumnFamily(cfDevices.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfDevices.getName());
            try {
                keyspace.createColumnFamily(cfDevices, ImmutableMap.<String, Object>builder()
                        .put("key_validation_class", UUIDTYPE.getTypeName())
                        .put("comparator_type", "CompositeType(UTF8Type,UTF8Type)")
                        .build());
            } catch (ConnectionException e) {
                LOGGER.error(
                        String.format("Can't create '%s' column family", cfDevices.getName()),
                        e);
                return;

            }
        }
    }

    public Keyspace getKeyspace() {
        return keyspace;
    }

    public ColumnFamily<String, UserProperty> getUsersCF() {
        return cfUsers;
    }

    public ColumnFamily<UUID, SensorProperty> getDevicesCF() {
        return cfDevices;
    }
}
