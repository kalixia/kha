package com.kalixia.ha.api

import com.kalixia.ha.model.Device
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.RGBLamp
import rx.Observable
import rx.Observer

class DevicesServiceImpl implements DevicesService {
    def device1 = new RGBLamp(UUID.randomUUID(), "device1")
    def device2 = new RGBLamp(UUID.randomUUID(), "device2")

    @Override
    def Observable<Device> findAllDevices(User user) {
        return Observable.create({ Observer<Device> observer ->
            try {
                observer.onNext(device1)
                observer.onNext(device2)
                observer.onCompleted()
            } catch (Exception e) {
                observer.onError(e)
            }
        })
    }

    @Override
    def Observable<Device> findDeviceById(UUID id) {
        return Observable.create({ Observer<Device> observer ->
            try {
                observer.onNext(device1)
                observer.onCompleted()
            } catch (Exception e) {
                observer.onError(e)
            }
        })
    }

}
