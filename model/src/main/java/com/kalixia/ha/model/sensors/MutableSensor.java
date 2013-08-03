package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public class MutableSensor<Q extends Quantity> implements Sensor<Q> {
    private String name;
    private Unit<Q> unit;
    private DataPoint<Q> lastValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit<Q> getUnit() {
        return unit;
    }

    public void setUnit(Unit<Q> unit) {
        this.unit = unit;
    }

    @Override
    public DataPoint<Q> getLastValue() {
        return lastValue;
    }

    public void setLastValue(DataPoint<Q> lastValue) {
        this.lastValue = lastValue;
    }
}
