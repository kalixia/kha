package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.sensors.CounterSensor
import com.kalixia.ha.model.sensors.Sensor
import com.kalixia.ha.model.sensors.SensorBuilder
import com.kalixia.ha.model.sensors.SensorCreator

import static java.lang.String.format

class EcoDeviceSensorCreator implements SensorCreator<EcoDevice> {
    @Override
    String getDeviceType() {
        return EcoDevice.TYPE
    }

    @Override
    Sensor createFromBuilder(SensorBuilder<EcoDevice> builder) {
        switch (builder.type) {
            case 'teleinfo-1':
                return new TeleinfoSensor(builder.name, TeleinfoSensorSlot.TELEINFO1);
            case 'teleinfo-2':
                return new TeleinfoSensor(builder.name, TeleinfoSensorSlot.TELEINFO2);
            case 'counter1':
                return new CounterSensor(builder.name, builder.unit) {
                    @Override
                    String getType() {
                        return "teleinfo-counter1"
                    }
                }
            case 'counter2':
                return new CounterSensor(builder.name, builder.unit) {
                    @Override
                    String getType() {
                        return "teleinfo-counter2"
                    }
                }
            default:
                throw new IllegalArgumentException(format("Can't process type %s", builder.getType()))
        }
    }
}
