package com.kalixia.ha.devices.gce.ecodevices

import com.google.common.collect.Sets
import com.kalixia.ha.model.quantity.WattsPerHour
import com.kalixia.ha.model.sensors.AggregatedSensor
import com.kalixia.ha.model.sensors.CounterSensor
import com.kalixia.ha.model.sensors.DataPoint
import com.kalixia.ha.model.sensors.GaugeSensor
import com.kalixia.ha.model.sensors.Sensor

import javax.measure.Measure
import javax.measure.quantity.Power
import javax.measure.unit.Unit

import static javax.measure.unit.SI.WATT

class TeleinfoSensor implements AggregatedSensor<WattsPerHour> {
    private final String name
    private final TeleinfoSensorSlot slot
    private final GaugeSensor<Power> instant
    private final CounterSensor<WattsPerHour> hp
    private final CounterSensor<WattsPerHour> hc
    private final Set<Sensor<WattsPerHour>> sensors

    public TeleinfoSensor(String name, TeleinfoSensorSlot slot) {
        this.name = name
        this.slot = slot
        instant = new GaugeSensor<>(String.format("%s (W)", name), WATT,
                Measure.valueOf(0L, WATT), Measure.valueOf(10000L, WATT))
        hp = new CounterSensor<>(String.format("%s (HP)", name), WattsPerHour.UNIT)
        hc = new CounterSensor<>(String.format("%s (HC)", name), WattsPerHour.UNIT)
        sensors = Sets.newHashSet(instant, hp, hc)
    }

    @Override
    public String getName() {
        return name
    }

    @Override
    public Unit<WattsPerHour> getUnit() {
        throw new UnsupportedOperationException("The teleinfo is an aggregate sensor. It has no unit in itself!")
    }

    @Override
    DataPoint<WattsPerHour> getLastValue() {
        throw new UnsupportedOperationException("The teleinfo is an aggregate sensor. It has no value in itself!")
    }

    @Override
    String getSensorsPrefix() {
        return name
    }

    public Set<Sensor<WattsPerHour>> getSensors() {
        return sensors
    }

    public TeleinfoSensorSlot getSlot() {
        return slot
    }

}