package com.kalixia.ha.api.cassandra;

import com.google.common.collect.ImmutableMap;
import com.kalixia.ha.api.DevicesDao;
import com.kalixia.ha.api.SensorsDao;
import com.kalixia.ha.model.Device;
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
import rx.Observable;

import java.util.Date;
import java.util.UUID;

public class CassandraDevicesDao<T> implements DevicesDao {
//    private final ColumnFamily<UUID, String> cfSensors;
//    private final ColumnFamily<UUID, Date> cfSensorsData;
//    private final ColumnFamily<String, Integer> cfSensorsStats;

    public CassandraDevicesDao(Keyspace keyspace) throws ConnectionException {
//        cfSensors = new ColumnFamily<>("Sensors", TimeUUIDSerializer.get(), StringSerializer.get());
//        cfSensorsData = new ColumnFamily<>("SensorsData", TimeUUIDSerializer.get(), DateSerializer.get());
//        cfSensorsStats = new ColumnFamily<>("SensorsStats", StringSerializer.get(), IntegerSerializer.get());

        // analyse du schéma du keyspace
        KeyspaceDefinition keyspaceDefinition;
        try {
            keyspaceDefinition = keyspace.describeKeyspace();
        } catch (ConnectionException e) {
            // création du keyspace s'il n'existe pas
            System.out.printf("Keyspace '%s' does not exists. Creation in progress...%n", keyspace.getKeyspaceName());
            keyspace.createKeyspace(ImmutableMap.<String, Object>builder()
                    .put("strategy_options", ImmutableMap.<String, Object>builder()
                            .put("replication_factor", "2")
                            .build())
                    .put("strategy_class", "SimpleStrategy")
                    .build()
            );
            keyspaceDefinition = keyspace.describeKeyspace();
        }

        /*
        // création de la CF Sensors si elle n'existe pas
        if (keyspaceDefinition.getColumnFamily(cfSensors.getName()) == null) {
            // column family is missing -- create it!
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
        */
    }

    @Override
    public Observable<? extends Device> findAllDevices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
