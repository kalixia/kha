package com.kalixia.ha.model.sensors;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.measure.unit.Unit;

@ApiModel("SensorMetadata")
public interface SensorMetadata {

    @ApiModelProperty("name of the sensor")
    String getName();

    @ApiModelProperty("unit of the sensor")
    Unit getUnit();

}
