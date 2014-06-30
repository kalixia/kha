package com.kalixia.ha.devices.zibase.zapi2;

import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.devices.DeviceCreator;

public class ZibaseDeviceCreator implements DeviceCreator {

    @Override
    public String getDeviceType() {
        return ZibaseDevice.TYPE;
    }

    @Override
    public Device createFromBuilder(DeviceBuilder builder) {
        return new ZibaseDevice(builder);
    }

}
