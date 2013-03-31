package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.Capability;
import com.kalixia.ha.model.Device;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract device class easing code a little bit.
 */
abstract class AbstractDevice implements Device {
    private final UUID id;
    private final String name;
    private final Set<Class<? extends Capability>> capabilities;

    protected AbstractDevice(UUID id, String name, Class<? extends Capability>... capabilities) {
        this.id = id;
        this.name = name;
        this.capabilities = Collections.unmodifiableSet(
                new HashSet<Class<? extends Capability>>(Arrays.asList(capabilities)));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Class<? extends Capability>> getCapabilities() {
        return capabilities;
    }

    @Override
    public boolean hasCapability(Class<? extends Capability> capability) {
        return capabilities.contains(capability);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractDevice{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", capabilities=").append(capabilities);
        sb.append('}');
        return sb.toString();
    }
}
