package com.kalixia.ha.dao;

import com.kalixia.ha.model.sensors.DataPoint;

import java.util.UUID;

public interface SensorsDao<T> {
    DataPoint<T> getLastValue(UUID sensorID);
}
