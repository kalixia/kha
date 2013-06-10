package com.kalixia.ha.dao;

import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceID;
import rx.Observable;

public interface DevicesDao {
    Device findById(DeviceID id) throws Exception;
    Device findByName(String name) throws Exception;
    Observable<? extends Device> findAllDevicesOfUser(String username) throws Exception;
    void save(Device device) throws Exception;
    void delete(DeviceID id);
}
