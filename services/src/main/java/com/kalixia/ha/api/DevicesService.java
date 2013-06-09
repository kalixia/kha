package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import rx.Observable;

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
     * @return the device as an {@link Observable}
     */
    Observable<? extends Device> findDeviceById(UUID id);

    /**
     * Creates a new device.
     * @param device the device to create
     * @return the created device as an {@link Observable}
     */
    void saveDevice(Device device);

}
