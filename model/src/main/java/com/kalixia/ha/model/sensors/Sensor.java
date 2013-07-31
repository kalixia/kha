package com.kalixia.ha.model.sensors;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public interface Sensor<Q extends Quantity> {
    String getName();
    Unit<Q> getUnit();
}
