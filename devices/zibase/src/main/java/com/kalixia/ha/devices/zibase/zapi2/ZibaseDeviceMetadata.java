package com.kalixia.ha.devices.zibase.zapi2;

import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.sensors.SensorMetadata;

import java.util.Set;

import static java.util.Collections.emptySet;

public class ZibaseDeviceMetadata implements DeviceMetadata {
    @Override
    public String getName() {
        return "Zibase";
    }

    @Override
    public String getType() {
        return "zibase";
    }

    @Override
    public String getLogo() {
        return "zibase.png";
    }

    @Override
    public Set<SensorMetadata> getSensorsMetadata() {
        return emptySet();
    }
}
