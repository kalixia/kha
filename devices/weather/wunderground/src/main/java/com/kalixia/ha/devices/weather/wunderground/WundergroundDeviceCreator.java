package com.kalixia.ha.devices.weather.wunderground;

import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.devices.DeviceCreator;
import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.sensors.SensorMetadata;

import java.util.Collections;
import java.util.Set;

public class WundergroundDeviceCreator implements DeviceCreator {

    @Override
    public String getDeviceType() {
        return WundergroundDevice.TYPE;
    }

    @Override
    public Device createFromBuilder(DeviceBuilder builder) {
        return new WundergroundDevice(builder);
    }
}
