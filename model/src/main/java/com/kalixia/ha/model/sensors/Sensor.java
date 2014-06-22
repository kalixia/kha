package com.kalixia.ha.model.sensors;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

@ApiModel("Sensor")
public interface Sensor<Q extends Quantity> {

    @ApiModelProperty("name of the sensor")
    String getName();

    @ApiModelProperty("unit of the sensor")
    Unit<Q> getUnit();

    @ApiModelProperty("last value of the sensor")
    DataPoint<Q> getLastValue();

}
