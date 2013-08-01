package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.Auditable;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.sensors.Sensor;

import java.util.Set;
import java.util.UUID;

public interface Device extends Auditable {
    UUID getId();
    User getOwner();
    String getName();

    Set<Class<? extends Capability>> getCapabilities();
    boolean hasCapability(Class<? extends Capability> capability);

    Device addSensor(Sensor sensor);
    Device addSensors(Sensor... sensors);
    Set<? extends Sensor> getSensors();
}
