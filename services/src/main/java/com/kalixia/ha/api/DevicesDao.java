package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import rx.Observable;

import java.util.UUID;

public interface DevicesDao {
    Observable<? extends Device> findAllDevices();
    Device findById(UUID id);
}
