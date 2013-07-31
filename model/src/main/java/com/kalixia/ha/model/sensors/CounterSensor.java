package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * Sensor based on a simple counter.
 */
public class CounterSensor<Q extends Quantity> implements Sensor {
    private final String name;
    private final Unit<Q> unit;

    public CounterSensor(String name, Unit<Q> unit) {
        this.name = name;
        this.unit = unit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Unit<Q> getUnit() {
        return unit;
    }
}
