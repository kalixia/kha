package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.Device;

import java.util.UUID;

public class DevicesFactory {
    private static DevicesFactory ourInstance = new DevicesFactory();

    public static DevicesFactory getInstance() {
        return ourInstance;
    }

    private DevicesFactory() {
    }

    public static Device createDevice(String name, Class<? extends Device> deviceType) {
        if (deviceType.isAssignableFrom(RGBLamp.class))
            return new RGBLamp(UUID.randomUUID(), name);
        else
            throw new IllegalArgumentException("Unsupported device type " + deviceType.getName());
    }
}
