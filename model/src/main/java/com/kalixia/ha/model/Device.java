package com.kalixia.ha.model;

import java.util.List;
import java.util.UUID;

public class Device {
    private final UUID id;
    private final String name;
    private final List<Capability> capabilities;

    public Device(UUID id, String name, List<Capability> capabilities) {
        this.id = id;
        this.name = name;
        this.capabilities = capabilities;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Capability> getCapabilities() {
        return capabilities;
    }
}
