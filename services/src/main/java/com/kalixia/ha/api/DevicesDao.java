package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import rx.Observable;

import java.util.UUID;

public interface DevicesDao {
    Device findById(UUID id) throws ConnectionException;
    Device findByName(String name) throws ConnectionException;
    Observable<? extends Device> findAllDevicesOfUser(User user) throws ConnectionException;
    void save(Device device) throws ConnectionException;
}
