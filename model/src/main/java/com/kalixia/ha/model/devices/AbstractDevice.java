package com.kalixia.ha.model.devices;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.kalixia.ha.model.AbstractAuditable;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.configuration.Configuration;
import com.kalixia.ha.model.configuration.ConfigurationBuilder;
import com.kalixia.ha.model.internal.CapabilitiesSerializer;
import com.kalixia.ha.model.internal.UserReferenceSerializer;
import com.kalixia.ha.model.sensors.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract device class easing code a little bit.
 */
public abstract class AbstractDevice<C extends Configuration> extends AbstractAuditable implements Device<C> {
    private final UUID id;
    private final String name;
    private final User owner;
    private final Set<Class<? extends Capability>> capabilities;
    private final Set<Sensor> sensors;
    protected C configuration;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected abstract void init(C configuration);
    protected abstract String getConfigurationFilename();
    protected abstract Class<C> getConfigurationClass();

    protected AbstractDevice(DeviceBuilder builder,
                             Class<? extends Capability>... capabilities) {
        super(builder.getCreationDate(), builder.getLastUpdateDate());
        this.id = builder.getId();
        this.name = builder.getName();
        this.owner = builder.getOwner();
        this.capabilities = Collections.unmodifiableSet(Sets.newHashSet(capabilities));
        this.sensors = Sets.newHashSet();
        reloadConfiguration();
    }

    public UUID getId() {
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
        return Collections.unmodifiableSet(capabilities);
    }

    @Override
    public boolean hasCapability(Class<? extends Capability> capability) {
        return capabilities.contains(capability);
    }

    @Override
    public Device addSensor(Sensor sensor) {
        sensors.add(sensor);
        return this;
    }

    @Override
    public Device addSensors(Sensor... sensors) {
        checkNotNull(sensors);
        for (Sensor sensor : sensors)
            addSensor(sensor);
        return this;
    }

    @Override
    public Set<? extends Sensor> getSensors() {
        return sensors;
    }

    public C getConfiguration() {
        return configuration;
    }

    @Override
    public void reloadConfiguration() {
        try {
            configuration = ConfigurationBuilder.loadConfiguration(
                    name, getConfigurationFilename(), getConfigurationClass());
            if (configuration == null) {
                throw new IllegalStateException("Can't initialize device because of missing configuration");
            }
            init(configuration);
        } catch (IOException e) {
            LOGGER.error("Can't load configuration of device '{}'", name, e);
        }
    }

    @Override
    public void saveConfiguration(Configuration configuration) {
        try {
            ConfigurationBuilder.saveConfiguration(this, configuration);
            reloadConfiguration();
        } catch (IOException e) {
            LOGGER.error("Can't save configuration of device '{}'", name, e);
        }
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
