package com.kalixia.ha.devices.gce.ecodevices;

import com.google.common.collect.Sets;
import com.kalixia.ha.model.quantity.WattsPerHour;
import com.kalixia.ha.model.sensors.AggregatedSensor;
import com.kalixia.ha.model.sensors.CounterSensor;
import com.kalixia.ha.model.sensors.Sensor;

import javax.measure.unit.Unit;
import java.util.Set;

public class TeleinfoSensor implements AggregatedSensor<WattsPerHour> {
    private final TeleinfoSensorSlot slot;
    private final CounterSensor<WattsPerHour> hp;
    private final CounterSensor<WattsPerHour> hc;
    private final Set<Sensor<WattsPerHour>> sensors;

    public TeleinfoSensor(TeleinfoSensorSlot slot) {
        this.slot = slot;
        hp = new CounterSensor<>(String.format("%s (HP)", slot.getName()), WattsPerHour.UNIT);
        hc = new CounterSensor<>(String.format("%s (HC)", slot.getName()), WattsPerHour.UNIT);
        sensors = Sets.newLinkedHashSet();
        sensors.add(hp);
        sensors.add(hc);
    }

    @Override
    public String getName() {
        return getSlot().getName();
    }

    @Override
    public Unit<WattsPerHour> getUnit() {
        return WattsPerHour.UNIT;
    }

    public Set<Sensor<WattsPerHour>> getSensors() {
        return sensors;
    }

    public TeleinfoSensorSlot getSlot() {
        return slot;
    }

}
