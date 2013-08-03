package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import java.util.Observable;

/**
 * Sensor based on a simple counter.
 */
public class CounterSensor<Q extends Quantity> extends BasicSensor<Q> {
    public CounterSensor(String name, Unit<Q> unit) {
        super(name, unit);
    }
}
