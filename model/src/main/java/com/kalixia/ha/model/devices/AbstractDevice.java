package com.kalixia.ha.model.devices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.sensors.Sensor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract device class easing code a little bit.
 */
abstract class AbstractDevice<K> implements Device<K> {
    private final K id;
    private final String name;
    private final User owner;
    private final Set<Class<? extends Capability>> capabilities;
    private final Set<? extends Sensor> sensors;

    protected AbstractDevice(K id, String name, User owner, Class<? extends Capability>... capabilities) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.capabilities = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(capabilities)));
        this.sensors = new HashSet<>();
    }

    @JsonIgnore
    public K getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    // TODO: override serialization -- should only send owner username!
    public User getOwner() {
        return owner;
    }

    public Set<Class<? extends Capability>> getCapabilities() {
        return capabilities;
    }

    @Override
    public boolean hasCapability(Class<? extends Capability> capability) {
        return capabilities.contains(capability);
    }

    @Override
    public Set<? extends Sensor> getSensors() {
        return sensors;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AbstractDevice{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", owner='").append(owner).append('\'');
        sb.append(", capabilities=").append(capabilities);
        sb.append('}');
        return sb.toString();
    }
}
