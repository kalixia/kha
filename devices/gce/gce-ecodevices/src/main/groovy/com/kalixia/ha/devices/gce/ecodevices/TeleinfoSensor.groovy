package com.kalixia.ha.devices.gce.ecodevices

import com.google.common.collect.Sets
import com.kalixia.ha.model.quantity.WattsPerHour
import com.kalixia.ha.model.sensors.AbstractSensor
import com.kalixia.ha.model.sensors.AggregatedSensor
import com.kalixia.ha.model.sensors.CounterSensor
import com.kalixia.ha.model.sensors.DataPoint
import com.kalixia.ha.model.sensors.GaugeSensor
import com.kalixia.ha.model.sensors.Sensor

import javax.measure.Measure
import javax.measure.quantity.Power
import javax.measure.unit.Unit

import static javax.measure.unit.SI.WATT

class TeleinfoSensor extends AbstractSensor<WattsPerHour> implements AggregatedSensor<WattsPerHour> {
    private final String name
    private final TeleinfoSensorSlot slot
    private final GaugeSensor<Power> instant
    private final CounterSensor<WattsPerHour> hp
    private final CounterSensor<WattsPerHour> hc
    private final Set<Sensor> sensors

    public TeleinfoSensor(String name, TeleinfoSensorSlot slot) {
        this.name = name
        this.slot = slot
        instant = new GaugeSensor<Power>(String.format("%s (W)", name), WATT,
                Measure.valueOf(0L, WATT), Measure.valueOf(10000L, WATT)) {
            @Override
            String getType() {
                return "teleinfo-${slot.slug}-watts"
            }
        }
        hp = new CounterSensor<WattsPerHour>(String.format("%s (HP)", name), WattsPerHour.UNIT) {
            @Override
            String getType() {
                return "teleinfo-${slot.slug}-hp"
            }
        }
        hc = new CounterSensor<WattsPerHour>(String.format("%s (HC)", name), WattsPerHour.UNIT) {
            @Override
            String getType() {
                return "teleinfo-${slot.slug}-hc"
            }
        }
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
    String getType() {
        return "teleinfo-${slot.slug}"
    }

    @Override
    String getSensorsPrefix() {
        return name
    }

    public Set<Sensor> getSensors() {
        return sensors
    }

    public TeleinfoSensorSlot getSlot() {
        return slot
    }

}