package com.kalixia.ha.api;

import com.kalixia.ha.model.devices.DeviceID;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.RGBLamp;

public class DevicesFactory {
    private static DevicesFactory ourInstance = new DevicesFactory();

    public static DevicesFactory getInstance() {
        return ourInstance;
    }

    private DevicesFactory() {
    }

    public static Device createDevice(String name, User owner, Class<? extends Device> deviceType) {
        if (deviceType.isAssignableFrom(RGBLamp.class))
            return new RGBLamp(new DeviceID(owner.getUsername(), name), name, owner);
        else
            throw new IllegalArgumentException("Unsupported device type " + deviceType.getName());
    }
}
