package com.kalixia.ha.dao;

import com.kalixia.ha.model.sensors.DataPoint;
import org.joda.time.DateTime;
import org.joda.time.Period;

import javax.measure.quantity.Quantity;
import java.util.List;
import java.util.UUID;

public interface SensorsDao<Q extends Quantity> {
    DataPoint<Q> getLastValue(UUID sensorID);
    List<DataPoint<Q>> getHistory(DateTime from, DateTime to, Period period);
}
