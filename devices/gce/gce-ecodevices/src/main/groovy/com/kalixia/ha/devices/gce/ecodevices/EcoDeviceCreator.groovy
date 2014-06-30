package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.devices.DeviceCreator

class EcoDeviceCreator implements DeviceCreator {

    @Override
    String getDeviceType() {
        return EcoDevice.TYPE
    }

    @Override
    Device createFromBuilder(DeviceBuilder builder) {
        return new EcoDevice(builder);
    }

}
