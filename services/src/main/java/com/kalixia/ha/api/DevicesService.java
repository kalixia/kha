package com.kalixia.ha.api;

import com.kalixia.ha.model.User;
import com.kalixia.ha.model.configuration.Configuration;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceMetadata;
import rx.Observable;

import java.beans.IntrospectionException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface DevicesService {

    /**
     * Returns all {@link Device}s of the given {@link User}.
     * @param username the username/login of the {@link User}
     * @return the list of devices as an {@link Observable} of {@link Device}s
     */
    Observable<? extends Device> findAllDevicesOfUser(String username);

    /**
     * Return the {@link Device} having the specified <tt>id</tt>.
     * @param id the ID of the {@link Device} to search for
     * @return the device
     */
    Optional<Device> findDeviceById(UUID id);

    /**
     * Return the {@link Device} having the specified <tt>ownerUsername</tt> and <tt>name</tt>.
     * @param ownerUsername the login of the owner
     * @param name the name of the device
     * @return the device
     */
    Optional<Device> findDeviceByName(String ownerUsername, String name);

    /**
     * Return an {@link Observable} of supported {@link Device}s, via the {@link DeviceMetadata}s
     * @return the metadata of the supported devices
     */
    Observable<DeviceMetadata> findAllSupportedDevices();

    /**
     * Create a device for <tt>owner</tt> by specifying its <tt>name</tt> and <tt>type</tt>.
     * @param owner the owner of the device to create
     * @param name the name of the created device
     * @param type the type of device to create
     * @return the create device
     * @throws IllegalArgumentException if tne type of device is not supported
     */
    Device create(User owner, String name, String type);

    Device configure(UUID id, Map configurationData);

    /**
     * Creates a new device.
     * @param device the device to create
     */
    void saveDevice(Device device);

    /**
     * Delete the {@link Device} having the specified <tt>id</tt>.
     * @param id the ID of the {@link Device} to delete
     */
    void deleteDevice(UUID id);
}
