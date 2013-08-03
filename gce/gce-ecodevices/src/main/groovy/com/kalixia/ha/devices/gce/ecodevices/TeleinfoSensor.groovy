package com.kalixia.ha.devices.gce.ecodevices

import com.google.common.collect.Sets
import com.kalixia.ha.model.quantity.WattsPerHour
import com.kalixia.ha.model.sensors.AggregatedSensor
import com.kalixia.ha.model.sensors.CounterSensor
import com.kalixia.ha.model.sensors.DataPoint
import com.kalixia.ha.model.sensors.Sensor

import javax.measure.unit.Unit

class TeleinfoSensor implements AggregatedSensor<WattsPerHour> {
    private final String name
    private final TeleinfoSensorSlot slot
    private final CounterSensor<WattsPerHour> hp
    private final CounterSensor<WattsPerHour> hc
    private final Set<Sensor<WattsPerHour>> sensors

    public TeleinfoSensor(String name, TeleinfoSensorSlot slot) {
        this.name = name
        this.slot = slot
        hp = new CounterSensor<>(String.format("%s (HP)", name), WattsPerHour.UNIT)
        hc = new CounterSensor<>(String.format("%s (HC)", name), WattsPerHour.UNIT)
        sensors = Sets.newLinkedHashSet()
        sensors.add(hp)
        sensors.add(hc)
    }

    @Override
    public String getName() {
        return name
    }

    @Override
    public Unit<WattsPerHour> getUnit() {
        return WattsPerHour.UNIT
    }

    @Override
    DataPoint<WattsPerHour> getLastValue() {
        throw new UnsupportedOperationException("The teleinfo is an aggregate sensor. It has no value in itself!")
    }

    public Set<Sensor<WattsPerHour>> getSensors() {
        return sensors
    }

    public TeleinfoSensorSlot getSlot() {
        return slot
    }

}