package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.User;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import rx.Observable;

import java.util.Set;
import java.util.UUID;

public interface DevicesDao<K> {
    Device<K> findById(K id) throws ConnectionException;
    Device<K> findByName(String name) throws ConnectionException;
    Set<? extends Device<K>> findAllDevicesOfUser(String username) throws ConnectionException;
//    Observable<? extends Device<K>> findAllDevicesOfUser(String username) throws ConnectionException;
    void save(Device<K> device) throws ConnectionException;
}
