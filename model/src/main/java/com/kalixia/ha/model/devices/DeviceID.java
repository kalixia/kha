package com.kalixia.ha.model.devices;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeviceID {
    private final String owner;
    private final String deviceName;

    public DeviceID(String owner, String deviceName) {
        checkNotNull(owner, "Device owner can't be null");
        checkNotNull(deviceName, "Device name can't be null");
        this.owner = owner;
        this.deviceName = deviceName;
    }

    public String getOwner() {
        return owner;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getRowKey() {
        StringBuilder builder = new StringBuilder();
        return builder.append(owner).append('-').append(deviceName).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeviceID deviceID = (DeviceID) o;

        if (!deviceName.equals(deviceID.deviceName)) return false;
        if (!owner.equals(deviceID.owner)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + deviceName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeviceID{");
        sb.append("owner='").append(owner).append('\'');
        sb.append(", deviceName='").append(deviceName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
