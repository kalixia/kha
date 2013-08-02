package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class BasicSensor<Q extends Quantity> implements Sensor<Q> {
    private final String name;
    private final Unit<Q> unit;

    public BasicSensor(String name, Unit<Q> unit) {
        this.name = name;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public Unit<Q> getUnit() {
        return unit;
    }
}
