package com.kalixia.ha.api.cassandra;

public class DeviceRK {
    private final String owner;
    private final String deviceName;

    public DeviceRK(String owner, String deviceName) {
        // TODO: check if owner and deviceName are not null and not empty!
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

        DeviceRK deviceRK = (DeviceRK) o;

        if (!deviceName.equals(deviceRK.deviceName)) return false;
        if (!owner.equals(deviceRK.owner)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + deviceName.hashCode();
        return result;
    }

}
