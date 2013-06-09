package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import rx.Observable;

import java.util.UUID;

public interface DevicesDao<K> {
    Device<K> findById(K id) throws ConnectionException;
    Device<K> findByName(String name) throws ConnectionException;
    Observable<? extends Device<K>> findAllDevicesOfUser(User user) throws ConnectionException;
    void save(Device<K> device) throws ConnectionException;
}
