package com.kalixia.ha.api;

import com.kalixia.ha.api.cassandra.DeviceRK;
import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.RGBLamp;
import com.netflix.astyanax.util.TimeUUIDUtils;

public class DevicesFactory {
    private static DevicesFactory ourInstance = new DevicesFactory();

    public static DevicesFactory getInstance() {
        return ourInstance;
    }

    private DevicesFactory() {
    }

    public static Device createDevice(String name, User owner, Class<? extends Device> deviceType) {
        if (deviceType.isAssignableFrom(RGBLamp.class))
            return new RGBLamp(new DeviceRK(owner.getUsername(), name), name, owner);
        else
            throw new IllegalArgumentException("Unsupported device type " + deviceType.getName());
    }
}
