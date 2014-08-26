package com.kalixia.ha.model.sensors;

import org.joda.time.Instant;

import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public abstract class TemperatureSensor extends AbstractSensor<Temperature> {
    public static final String NAME = "temperature";
    public static final Unit<Temperature> UNIT = SI.CELSIUS;

    protected abstract Float getCelsius();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Unit<Temperature> getUnit() {
        return UNIT;
    }

    @Override
    public DataPoint<Temperature> getLastValue() {
        return new DataPoint<>(Measure.valueOf(getCelsius(), UNIT), Instant.now());
    }
}
