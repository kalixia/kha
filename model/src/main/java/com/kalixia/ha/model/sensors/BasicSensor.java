package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class BasicSensor<Q extends Quantity> implements Sensor<Q> {
    private final String name;
    private final Unit<Q> unit;
    private DataPoint<Q> lastValue;

    public BasicSensor(String name, Unit<Q> unit) {
        this.name = name;
        this.unit = unit;
    }

    public BasicSensor(String name, Unit<Q> unit, DataPoint<Q> lastValue) {
        this(name, unit);
        setLastValue(lastValue);
    }

    public String getName() {
        return name;
    }

    public Unit<Q> getUnit() {
        return unit;
    }

    @Override
    public DataPoint<Q> getLastValue() {
        return lastValue;
    }

    public void setLastValue(DataPoint<Q> lastValue) {
        this.lastValue = lastValue;
    }

    @Override
    public String toString() {
        return "BasicSensor{" +
                "name='" + name + '\'' +
                ", unit=" + unit +
                ", lastValue=" + lastValue +
                '}';
    }
}
