package com.kalixia.ha.model.devices;

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
