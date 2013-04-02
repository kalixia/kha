package com.kalixia.ha.model.sensors;

public interface Sensor<T> {
    T getValue();
    DataPoint<T> getLastDataPoint();
}
