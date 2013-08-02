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
        TELEINFO1("Téléinfo 1", "teleinfo1.xml"), TELEINFO2("Téléinfo 2", "teleinfo2.xml");

        private final String name;
        private final String slug;

        TeleinfoName(String name, String slug) {
            this.name = name;
            this.slug = slug;
        }

        public String getName() {
            return name;
        }

        public String getSlug() {
            return slug;
        }
    }
}
