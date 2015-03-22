package com.kalixia.ha.dao;

import com.kalixia.ha.model.devices.Device;
import rx.Observable;

import java.util.Optional;
import java.util.UUID;

public interface DevicesDao {

    /**
     * Search for a device having the given ID.
     *
     * @param id the ID of the device to look for
     * @return the device if found, null otherwise
     */
    Optional<Device> findById(UUID id);

    /**
     * Search for a device having the given <tt>owner</tt> and <tt>name</tt>.
     *
     * @param ownerUsername the username of the owner of the device
     * @param name          the name of the device
     * @return the device if found, null otherwise
     */
    Optional<Device> findByOwnerAndName(String ownerUsername, String name);

    /**
     * Observe user's devices.
     *
     * @param username the username of the owner of the devices
     * @return an {@link Observable} of the user's devices
     */
    Observable<? extends Device> findAllDevicesOfUser(String username);

    /**
     * Save or update a device if it already exists.
     *
     * @param device the device to save or update
     */
    void save(Device<?> device);

    /**
     * Delete a device.
     *
     * @param id the ID of the device to delete
     */
    void delete(UUID id);

}
