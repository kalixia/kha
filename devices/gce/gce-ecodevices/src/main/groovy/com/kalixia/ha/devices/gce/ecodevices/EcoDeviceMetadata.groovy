package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.devices.DeviceMetadata
import com.kalixia.ha.model.quantity.WattsPerHour
import com.kalixia.ha.model.sensors.CounterSensorMetadata
import com.kalixia.ha.model.sensors.SensorMetadata

import javax.measure.unit.NonSI
import javax.measure.unit.SI
import javax.measure.unit.Unit

class EcoDeviceMetadata implements DeviceMetadata {
    @Override
    String getName() {
        return 'GCE EcoDevices'
    }

    @Override
    String getType() {
        return 'gce-ecodevices'
    }

    @Override
    String getLogo() {
        return 'gce-ecodevices.png'
    }

    @Override
    Set<SensorMetadata> getSensorsMetadata() {
        return [
                new TeleinfoMetadata(1),
                new TeleinfoMetadata(2),
                new CounterSensorMetadata("Water", NonSI.LITER),
                new CounterSensorMetadata("Gas", SI.CUBIC_METRE)
        ]
    }
}

class TeleinfoMetadata implements SensorMetadata {
    private final int slotNumber;

    def TeleinfoMetadata(slotNumber) {
        this.slotNumber = slotNumber
    }

    @Override
    String getName() {
        return "Téléinfo $slotNumber"
    }

    @Override
    Unit getUnit() {
        return WattsPerHour.UNIT
    }
}
