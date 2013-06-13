package com.kalixia.ha.model.devices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.internal.CapabilitiesSerializer;
import com.kalixia.ha.model.internal.UserReferenceSerializer;
import com.kalixia.ha.model.sensors.Sensor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract device class easing code a little bit.
 */
abstract class AbstractDevice implements Device {
    private final DeviceID id;
    private final String name;
    private final User owner;
    private final Set<Class<? extends Capability>> capabilities;
    private final Set<? extends Sensor> sensors;

    protected AbstractDevice(DeviceID id, String name, User owner, Class<? extends Capability>... capabilities) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.capabilities = Collections.unmodifiableSet(Sets.newHashSet(capabilities));
        this.sensors = Sets.newHashSet();
    }

    @JsonIgnore
    public DeviceID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    @JsonSerialize(using = UserReferenceSerializer.class)
    public User getOwner() {
        return owner;
    }

    @Override
    @JsonSerialize(using = CapabilitiesSerializer.class)
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
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("owner", owner)
                .add("capabilities", capabilities)
                .add("sensors", sensors)
                .toString();
    }
}
