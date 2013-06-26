package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.SensorsDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceID
import com.kalixia.ha.model.devices.RGBLamp
import com.kalixia.ha.model.sensors.DataPoint
import com.kalixia.ha.model.sensors.Sensor
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.MutationBatch
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.astyanax.ddl.KeyspaceDefinition
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.model.ColumnList
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer
import com.netflix.astyanax.serializers.StringSerializer
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.Period

import static com.google.common.base.Preconditions.checkArgument
import static com.google.common.base.Preconditions.checkNotNull

@Slf4j("LOGGER")
public class CassandraSensorsDao implements SensorsDao {
    private final Keyspace keyspace
    private final ColumnFamily<String, AnnotatedCompositeSerializer> cfDevices

    public CassandraSensorsDao(Keyspace keyspace) throws ConnectionException {
        this.keyspace = keyspace;

        AnnotatedCompositeSerializer<SensorProperty> sensorPropertySerializer =
                new AnnotatedCompositeSerializer<SensorProperty>(SensorProperty.class);
        cfDevices = new ColumnFamily<>("Devices", StringSerializer.get(), sensorPropertySerializer)

        // analyze keyspace schema
        KeyspaceDefinition keyspaceDefinition = CassandraUtils.getOrCreateKeyspace(keyspace)

        /*
        // create Devices CF if missing
        if (keyspaceDefinition.getColumnFamily(cfSensors.getName()) == null) {
            // column family is missing -- create it!
            LOGGER.warn("Creating missing Column Family '{}'", cfSensors.getName());
            keyspace.createColumnFamily(cfSensors, ImmutableMap.<String, Object>builder()
                    .put("default_validation_class", UTF8TYPE.getTypeName())
                    .put("key_validation_class", UTF8TYPE.getTypeName())
                    .put("comparator_type", UTF8TYPE.getTypeName())
                    .put("column_metadata", ImmutableMap.<String, Object>builder()
                            .put("name", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_sensor_name")
                                    .put("index_type", "KEYS")
                                    .build())
                            .put("device", ImmutableMap.<String, Object>builder()
                                    .put("validation_class", UTF8TYPE.getTypeName())
                                    .put("index_name", "idx_device_id")
                                    .put("index_type", "KEYS")
                                    .build())
                            .build())
                    .build());
        }
        */
    }

    @Override
    DataPoint getLastValue(UUID sensorID) {
        return null
    }

    @Override
    List<DataPoint> getHistory(DateTime from, DateTime to, Period period) {
        return null
    }

}

