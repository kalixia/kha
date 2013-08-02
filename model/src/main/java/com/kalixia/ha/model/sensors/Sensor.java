package com.kalixia.ha.model.sensors;

import javax.measure.Measurable;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;
import rx.Observable;

public interface Sensor<Q extends Quantity> {
    String getName();
    Unit<Q> getUnit();
}
