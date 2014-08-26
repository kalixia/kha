package com.kalixia.ha.devices.zibase.zapi2;

import com.kalixia.ha.model.sensors.BasicSensor;
import com.kalixia.ha.model.sensors.Sensor;
import com.kalixia.ha.model.sensors.SensorBuilder;
import com.kalixia.ha.model.sensors.SensorCreator;

import javax.measure.quantity.Quantity;

import static java.lang.String.format;

public class ZibaseDeviceSensorCreator implements SensorCreator<ZibaseDevice> {
    @Override
    public String getDeviceType() {
        return ZibaseDevice.TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Sensor<? extends Quantity> createFromBuilder(SensorBuilder<ZibaseDevice> builder) {
        switch (builder.getType()) {
            case "power":
            case "temperature":
            case "humidity":
                return new BasicSensor(builder.getName(), builder.getUnit(), builder.getLastValue()) {
                    @Override
                    public String getType() {
                        return builder.getType();
                    }
                };
            default:
                throw new IllegalArgumentException(format("Can't process type %s", builder.getType()));
        }
    }
}
