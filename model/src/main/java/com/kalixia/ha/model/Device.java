package com.kalixia.ha.model;

import java.util.List;
import java.util.UUID;

public interface Device {
    UUID getId();
    String getName();
    List<Capability> getCapabilities();
}
