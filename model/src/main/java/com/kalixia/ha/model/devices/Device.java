package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.sensors.Sensor;

import java.util.Set;
import java.util.UUID;

public interface Device {
    DeviceID getId();
    User getOwner();
    String getName();
    Set<Class<? extends Capability>> getCapabilities();
    boolean hasCapability(Class<? extends Capability> capability);
    Set<? extends Sensor> getSensors();
}
