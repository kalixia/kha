package com.kalixia.ha.model;

import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.sensors.Sensor;

import java.util.Set;
import java.util.UUID;

public interface Device {
    UUID getId();
    String getName();
    Set<Class<? extends Capability>> getCapabilities();
    boolean hasCapability(Class<? extends Capability> capability);
    Set<? extends Sensor> getSensors();
}
