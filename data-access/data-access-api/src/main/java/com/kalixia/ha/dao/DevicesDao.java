package com.kalixia.ha.dao;

import com.kalixia.ha.model.Device;
import rx.Observable;
import java.util.Set;

public interface DevicesDao<K> {
    Device<K> findById(K id) throws Exception;
    Device<K> findByName(String name) throws Exception;
    Observable<? extends Device<K>> findAllDevicesOfUser(String username) throws Exception;
    void save(Device<K> device) throws Exception;
}
