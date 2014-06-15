package com.kalixia.ha.devices.zibase.zapi2;

import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceMetadata;
import com.kalixia.ha.model.sensors.SensorMetadata;

import java.util.Set;
import java.util.UUID;

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

    @Override
    public Device createDevice(User owner, String name) {
        return new ZibaseDevice(UUID.randomUUID(), name, owner);
    }
}
