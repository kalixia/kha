package com.kalixia.ha.model.sensors;

import javax.measure.unit.Unit;

public class MutableSensor implements Sensor {
    private String name;
    private Unit unit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
