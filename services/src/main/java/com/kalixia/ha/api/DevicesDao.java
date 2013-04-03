package com.kalixia.ha.api;

import com.kalixia.ha.model.Device;
import rx.Observable;

public interface DevicesDao {
    Observable<? extends Device> findAllDevices();
}
