package com.kalixia.ha.model.devices;

public interface DeviceCreator {

    /**
     * The technical name of the device.
     */
    String getDeviceType();

    /**
     * Create a device for <tt>owner</tt> by specifying its <tt>name</tt> .
     *
     * @param builder the builder with all device parameters set
     * @return the created device
     */
    Device createFromBuilder(DeviceBuilder builder);

}
