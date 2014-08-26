package com.kalixia.ha.devices.rgblamp;

import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.devices.DeviceCreator;

public class RGBLampDeviceCreator implements DeviceCreator {
    @Override
    public String getDeviceType() {
        return RGBLamp.TYPE;
    }

    @Override
    public Device createFromBuilder(DeviceBuilder builder) {
        return new RGBLamp(builder);
    }
}
