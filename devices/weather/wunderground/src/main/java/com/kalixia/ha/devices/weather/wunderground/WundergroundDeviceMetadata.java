package com.kalixia.ha.devices.weather.wunderground;

import com.kalixia.ha.devices.weather.wunderground.sensors.WundergroundPressureSensor;
import com.kalixia.ha.devices.weather.wunderground.sensors.WundergroundTemperatureSensor;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.sensors.SensorMetadata;

import javax.measure.unit.Unit;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.Sets.newHashSet;

public class WundergroundDeviceMetadata implements DeviceMetadata {

    @Override
    public String getName() {
        return "Weather Underground";
    }

    @Override
    public String getType() {
        return "wunderground";
    }

    @Override
    public String getLogo() {
        return "wunderground.png";
    }

    @Override
    public Set<SensorMetadata> getSensorsMetadata() {
        SensorMetadata temperatureSensor = new SensorMetadata() {
            @Override
            public String getName() {
                return WundergroundTemperatureSensor.NAME;
            }

            @Override
            public Unit getUnit() {
                return WundergroundTemperatureSensor.UNIT;
            }
        };
        SensorMetadata pressureSensor = new SensorMetadata() {
            @Override
            public String getName() {
                return WundergroundPressureSensor.NAME;
            }

            @Override
            public Unit getUnit() {
                return WundergroundPressureSensor.UNIT;
            }
        };
        return newHashSet(temperatureSensor, pressureSensor);
    }

}
