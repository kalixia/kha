package com.kalixia.ha.model.quantity;

import javax.measure.quantity.Quantity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public interface WattsPerHour extends Quantity {
    @SuppressWarnings("unchecked")
    public final static Unit<WattsPerHour> UNIT = (Unit<WattsPerHour>) SI.WATT.divide(NonSI.HOUR);
}
