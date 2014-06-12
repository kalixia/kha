package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * Metadata for {@link CounterSensor}s.
 */
public class CounterSensorMetadata<Q extends Quantity> implements SensorMetadata {
    private final String name;
    private final Unit<Q> unit;

    public CounterSensorMetadata(String name, Unit<Q> unit) {
        this.name = name;
        this.unit = unit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Unit getUnit() {
        return unit;
    }
}
