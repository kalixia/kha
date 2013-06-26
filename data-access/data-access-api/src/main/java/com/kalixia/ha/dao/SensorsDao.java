package com.kalixia.ha.dao;

import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.sensors.DataPoint;
import com.kalixia.ha.model.sensors.Sensor;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;
import java.util.UUID;

public interface SensorsDao<T> {
    DataPoint<T> getLastValue(UUID sensorID);
    List<DataPoint<T>> getHistory(DateTime from, DateTime to, Period period);
}
