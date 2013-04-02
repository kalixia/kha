package com.kalixia.ha.api.cassandra;

import com.kalixia.ha.api.SensorsDao;
import com.kalixia.ha.model.sensors.DataPoint;
import com.netflix.astyanax.Keyspace;

import java.util.UUID;

public class CassandraSensorsDao<T> implements SensorsDao<T> {
    private final Keyspace keyspace;

    public CassandraSensorsDao(Keyspace keyspace) {
        this.keyspace = keyspace;
    }

    @Override
    public DataPoint<T> getLastValue(UUID p) {
        return null;
    }
}
