package com.kalixia.ha.model;

import java.util.Set;
import java.util.UUID;

public interface Device {
    UUID getId();
    String getName();
    Set<Class<? extends Capability>> getCapabilities();
    boolean hasCapability(Class<? extends Capability> capability);
}
