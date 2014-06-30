package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.User;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;

public class DeviceBuilder {
    private UUID id = UUID.randomUUID();
    private User owner;
    private String name;
    private DateTime creationDate = DateTime.now();
    private DateTime lastUpdateDate = DateTime.now();
    private String type;
    private final Map<String, DeviceCreator> devicesCreatorsMap;

    public DeviceBuilder() {
        devicesCreatorsMap = new HashMap<>();
        ServiceLoader.load(DeviceCreator.class)
                .forEach(creator -> devicesCreatorsMap.put(creator.getDeviceType(), creator));
    }

    public DeviceBuilder withID(UUID id) {
        this.id = id;
        return this;
    }

    public DeviceBuilder withOwner(User owner) {
        this.owner = owner;
        return this;
    }

    public DeviceBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DeviceBuilder withCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public DeviceBuilder withLastUpdateDate(DateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public DeviceBuilder ofType(String type) {
        this.type = type;
        return this;
    }

    public Device build() {
        DeviceCreator deviceCreator = devicesCreatorsMap.get(type);
        if (deviceCreator == null)
            throw new IllegalArgumentException(String.format("Can't build device of type %s", type));
        return deviceCreator.createFromBuilder(this);
    }

    public UUID getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getType() {
        return type;
    }

    public Map<String, DeviceCreator> getDevicesCreatorsMap() {
        return devicesCreatorsMap;
    }
}
