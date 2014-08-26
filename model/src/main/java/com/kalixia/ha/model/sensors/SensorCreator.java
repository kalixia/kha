package com.kalixia.ha.model.sensors;

import com.kalixia.ha.model.devices.Device;

import javax.measure.quantity.Quantity;

public interface SensorCreator<D extends Device> {

    /**
     * The technical name of the device having sensors.
     */
    String getDeviceType();

    /**
     * Create a sensor.
     *
     * @param builder the builder with all sensor parameters set
     * @return the created sensor
     */
    Sensor<? extends Quantity> createFromBuilder(SensorBuilder<D> builder);

}
