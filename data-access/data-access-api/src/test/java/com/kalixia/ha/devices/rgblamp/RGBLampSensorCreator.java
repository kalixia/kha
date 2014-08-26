package com.kalixia.ha.devices.rgblamp;

import com.kalixia.ha.model.sensors.BasicSensor;
import com.kalixia.ha.model.sensors.Sensor;
import com.kalixia.ha.model.sensors.SensorBuilder;
import com.kalixia.ha.model.sensors.SensorCreator;

import static java.lang.String.format;

public class RGBLampSensorCreator implements SensorCreator<RGBLamp> {
    @Override
    public String getDeviceType() {
        return RGBLamp.TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Sensor createFromBuilder(SensorBuilder<RGBLamp> builder) {
        switch (builder.getType()) {
            case "test-duration":
                return new BasicSensor(builder.getName(), builder.getUnit()) {
                    @Override
                    public String getType() {
                        return "test-duration";
                    }
                };
            default:
                throw new IllegalArgumentException(format("Can't process type '%s'", builder.getType()));
        }
    }
}
