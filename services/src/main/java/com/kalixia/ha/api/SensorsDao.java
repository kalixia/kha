package com.kalixia.ha.api;

import com.kalixia.ha.model.sensors.DataPoint;

import java.util.UUID;

public interface SensorsDao<T> {
    DataPoint<T> getLastValue(UUID p);
}
