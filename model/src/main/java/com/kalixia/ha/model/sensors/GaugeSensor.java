package com.kalixia.ha.model.sensors;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

/**
 * Sensor based on a simple counter.
 */
public class GaugeSensor<Q extends Quantity> extends BasicSensor<Q> {
    private Measurable<Q> min;
    private Measurable<Q> max;

    public GaugeSensor(String name, Unit<Q> unit, Measurable<Q> min, Measurable<Q> max) {
        super(name, unit);
        this.min = min;
        this.max = max;
    }

    public Measurable<Q> getMin() {
        return min;
    }

    public Measurable<Q> getMax() {
        return max;
    }
}
