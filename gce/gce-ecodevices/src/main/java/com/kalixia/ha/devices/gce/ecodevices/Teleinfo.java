package com.kalixia.ha.devices.gce.ecodevices;

import com.kalixia.ha.model.quantity.WattsPerHour;
import com.kalixia.ha.model.sensors.CounterSensor;

public class Teleinfo {
    private final TeleinfoName name;
    private final CounterSensor<?> hp;
    private final CounterSensor<?> hc;

    public Teleinfo(TeleinfoName name) {
        this.name = name;
        hp = new CounterSensor<>(String.format("%s (HP)", name), WattsPerHour.UNIT);
        hc = new CounterSensor<>(String.format("%s (HC)", name), WattsPerHour.UNIT);
    }

    public TeleinfoName getName() {
        return name;
    }

    public CounterSensor<?>[] getSensors() {
        return new CounterSensor[] { hp, hc };
    }

    public enum TeleinfoName {
        TELEINFO1("Téléinfo 1"), TELEINFO2("Téléinfo 2");

        private final String name;

        TeleinfoName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
