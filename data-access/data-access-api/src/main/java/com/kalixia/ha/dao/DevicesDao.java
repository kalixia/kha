package com.kalixia.ha.dao;

import com.kalixia.ha.model.devices.Device;
import rx.Observable;
import java.util.UUID;

public interface DevicesDao {
    Device findById(UUID id);
    Device findByOwnerAndName(String ownerUsername, String name);
    Observable<? extends Device> findAllDevicesOfUser(String username);
    void save(Device<?> device);
    void delete(UUID id);
}
