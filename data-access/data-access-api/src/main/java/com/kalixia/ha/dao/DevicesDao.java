package com.kalixia.ha.dao;

import com.kalixia.ha.model.devices.Device;
import rx.Observable;
import java.util.UUID;

public interface DevicesDao {
    Device findById(UUID id) throws Exception;
    Device findByOwnerAndName(String ownerUsername, String name) throws Exception;
    Observable<? extends Device> findAllDevicesOfUser(String username) throws Exception;
    void save(Device device) throws Exception;
    void delete(UUID id);
}
