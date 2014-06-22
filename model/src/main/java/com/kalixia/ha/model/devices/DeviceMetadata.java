package com.kalixia.ha.model.devices;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.sensors.SensorMetadata;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Set;

@ApiModel("DeviceMetadata")
public interface DeviceMetadata {

    /**
     * Exposes the default name for display purposes.
     * @return the default name
     */
    @ApiModelProperty("name of the device")
    String getName();

    /**
     * Exposes the technical name.
     * @return the technical name.
     */
    @ApiModelProperty("type of the device")
    String getType();

    /**
     * Indicates which image should be displayed to the end-user.
     * @return the logo for the device
     */
    @ApiModelProperty("logo of the device")
    String getLogo();

    /**
     * Exposes the supported sensors of this type of device.
     * @return the supported sensors
     */
    @JsonProperty("sensors")
    @ApiModelProperty("sensors metadata of the device")
    Set<SensorMetadata> getSensorsMetadata();

    /**
     * Create a device for <tt>owner</tt> by specifying its <tt>name</tt> .
     * @param owner the owner of the device to create
     * @param name the name of the created device
     * @return the created device
     */
    Device createDevice(User owner, String name);

}
