package com.kalixia.ha.api;

import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.RGBLamp;

import java.util.UUID;

public class DevicesFactory {
    private static DevicesFactory ourInstance = new DevicesFactory();

    public static DevicesFactory getInstance() {
        return ourInstance;
    }

    private DevicesFactory() {
    }

    public static Device createDevice(String name, User owner, Class<? extends Device> deviceType) {
        if (deviceType.isAssignableFrom(RGBLamp.class))
            return new RGBLamp(UUID.randomUUID(), name, owner);
        else
            throw new IllegalArgumentException("Unsupported device type " + deviceType.getName());
    }
}
