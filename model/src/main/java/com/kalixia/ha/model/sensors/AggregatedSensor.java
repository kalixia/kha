package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import java.util.SortedSet;

/**
 * Virtual sensor aggregating multiple sensors.
 * This kind of sensor is usually used when collecting multiple sensors values at once.
 * @param <Q>
 */
public interface AggregatedSensor<Q extends Quantity> extends Sensor<Q> {
    SortedSet<Sensor> getSensors();
}
