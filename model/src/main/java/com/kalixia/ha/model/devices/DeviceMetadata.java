package com.kalixia.ha.model.devices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.sensors.SensorMetadata;

import java.util.Set;

public interface DeviceMetadata {

    /**
     * Exposes the default name for display purposes.
     * @return the default name
     */
    String getName();

    /**
     * Exposes the technical name.
     * @return the technical name.
     */
    String getType();

    /**
     * Indicates which image should be displayed to the end-user.
     * @return the logo for the device
     */
    String getLogo();

    /**
     * Exposes the supported sensors of this type of device.
     * @return the supported sensors
     */
    @JsonProperty("sensors")
    Set<SensorMetadata> getSensorsMetadata();

    /**
     * Create a device for <tt>owner</tt> by specifying its <tt>name</tt> .
     * @param owner the owner of the device to create
     * @param name the name of the created device
     * @return the created device
     */
    Device createDevice(User owner, String name);

}
