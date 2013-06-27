package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.dao.SensorsDao
import com.kalixia.ha.model.sensors.DataPoint
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer
import groovy.util.logging.Slf4j
import org.joda.time.DateTime
import org.joda.time.Period

@Slf4j("LOGGER")
public class CassandraSensorsDao implements SensorsDao {
    private final SchemaDefinition schema
    private final ColumnFamily<String, AnnotatedCompositeSerializer> cfDevices

    public CassandraSensorsDao(SchemaDefinition schema) {
        this.schema = schema;
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

