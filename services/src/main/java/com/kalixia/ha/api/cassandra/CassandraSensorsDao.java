package com.kalixia.ha.api.cassandra;

import com.google.common.collect.ImmutableMap;
import com.kalixia.ha.api.SensorsDao;
import com.kalixia.ha.model.sensors.DataPoint;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.ddl.KeyspaceDefinition;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.ComparatorType;
import com.netflix.astyanax.serializers.DateSerializer;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.TimeUUIDSerializer;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class CassandraSensorsDao<T> implements SensorsDao<T> {
    private final Keyspace keyspace;
    private final ColumnFamily<UUID, String> cfSensors;
    private final ColumnFamily<UUID, Date> cfSensorsData;
    private final ColumnFamily<String, Integer> cfSensorsStats;
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraSensorsDao.class);

    public CassandraSensorsDao(Keyspace keyspace) throws ConnectionException {
        this.keyspace = keyspace;
        cfSensors = new ColumnFamily<>("Sensors", TimeUUIDSerializer.get(), StringSerializer.get());
        cfSensorsData = new ColumnFamily<>("SensorsData", TimeUUIDSerializer.get(), DateSerializer.get());
        cfSensorsStats = new ColumnFamily<>("SensorsStats", StringSerializer.get(), IntegerSerializer.get());
//        cfStatsRowKeyFormatter = new DateTimeFormatterBuilder()
//                .appendYear(4, 4)
//                .appendLiteral('/')
//                .appendMonthOfYear(2)
//                .appendLiteral('/')
//                .appendDayOfMonth(2)
//                .appendLiteral(':')
//                .appendHourOfDay(2)
//                .appendLiteral('h')
//                .toFormatter();

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace);

        // create Sensors CF if missing
        if (keyspaceDefinition.getColumnFamily(cfSensors.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfSensors.getName());
            keyspace.createColumnFamily(cfSensors, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.TIMEUUIDTYPE.getTypeName())
                    .put("comparator_type", ComparatorType.UTF8TYPE.getTypeName())
                    .put("column_metadata", ImmutableMap.<String, Object>builder()
                            .put("name", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", ComparatorType.UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_sensor_name")
                                    .put("index_type", "KEYS")
                                    .build())
                            .build())
                    .build());
        }

        // création de la CF SensorsData si elle n'existe pas
        if (keyspaceDefinition.getColumnFamily(cfSensorsData.getName()) == null) {
            // column family is missing -- create it!
            keyspace.createColumnFamily(cfSensorsData, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.DOUBLETYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.TIMEUUIDTYPE.getTypeName())
                    .put("comparator_type", ComparatorType.DATETYPE.getTypeName())
                    .build());
        }

        // création de la CF SensorsStats si elle n'existe pas
        if (keyspaceDefinition.getColumnFamily(cfSensorsStats.getName()) == null) {
            // column family is missing -- create it!
            keyspace.createColumnFamily(cfSensorsStats, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", ComparatorType.COUNTERTYPE.getTypeName())
                    .put("key_validation_class", ComparatorType.UTF8TYPE.getTypeName())
                    .put("comparator_type", ComparatorType.INTEGERTYPE.getTypeName())
                    .build());
        }
    }

    @Override
    public DataPoint<T> getLastValue(UUID id) {
        return null;
    }
}
