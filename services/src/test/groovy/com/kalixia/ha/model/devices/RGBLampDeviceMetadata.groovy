package com.kalixia.ha.model.devices

import com.kalixia.ha.model.sensors.SensorMetadata

import static java.util.Collections.emptySet

class RGBLampDeviceMetadata implements DeviceMetadata {
    @Override
    String getName() {
        return "RGB Lamp"
    }

    @Override
    String getType() {
        return "rgb-lamp"
    }

    @Override
    String getLogo() {
        return null
    }

    @Override
    Set<SensorMetadata> getSensorsMetadata() {
        return emptySet()
    }

}
