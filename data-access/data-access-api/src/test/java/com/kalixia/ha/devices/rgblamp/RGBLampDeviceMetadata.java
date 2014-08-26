package com.kalixia.ha.devices.rgblamp;

import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.sensors.SensorMetadata;

import java.util.Set;

import static java.util.Collections.emptySet;

public class RGBLampDeviceMetadata implements DeviceMetadata {
    @Override
    public String getName() {
        return "RGB Lamp";
    }

    @Override
    public String getType() {
        return "rgb-lamp";
    }

    @Override
    public String getLogo() {
        return null;
    }

    @Override
    public Set<SensorMetadata> getSensorsMetadata() {
        return emptySet();
    }
}
