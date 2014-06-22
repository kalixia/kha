package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.Auditable;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Capability;
import com.kalixia.ha.model.configuration.Configuration;
import com.kalixia.ha.model.sensors.Sensor;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Set;
import java.util.UUID;

@ApiModel("Device")
public interface Device<C extends Configuration> extends Auditable {
    @ApiModelProperty(value = "the ID of the device", required = true)
    UUID getId();

    @ApiModelProperty(value = "the owner of the device", required = true)
    User getOwner();

    @ApiModelProperty(value = "the name of the device", required = true)
    String getName();

    C getConfiguration();

    Set<Class<? extends Capability>> getCapabilities();
    boolean hasCapability(Class<? extends Capability> capability);

    Device addSensor(Sensor sensor);
    Device addSensors(Sensor... sensors);

    @ApiModelProperty(value = "the sensors of the device", required = false)
    Set<? extends Sensor> getSensors();
}
