package com.kalixia.ha.api

import com.kalixia.ha.model.Device
import com.kalixia.ha.model.devices.DevicesFactory
import com.kalixia.ha.model.devices.RGBLamp
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.subscriptions.Subscriptions

import javax.inject.Inject

class DevicesServiceImpl implements DevicesService {
    final DevicesDao dao

    def List<? extends Device> devices = [
        new RGBLamp(UUID.randomUUID(), "device1"),
        new RGBLamp(UUID.randomUUID(), "device2")
    ]

    DevicesServiceImpl(DevicesDao dao) {
        this.dao = dao;
    }

    @Override
    def Observable<? extends Device> findAllDevices() {
        return Observable.create({ observer ->
            try {
                observer.onNext(devices[0])
                observer.onNext(devices[1])
                observer.onCompleted()
            } catch (Exception e) {
                observer.onError(e)
            }
            return new Subscription() {
                @Override
                void unsubscribe() {
                    println "Unsubscribed!"
                }
            }
        })
    }

    @Override
    def Observable<Device> findDeviceById(UUID id) {
//        return Observable.just(dao.findById(id))
        return Observable.just(devices[0])
    }

    @Override
    def Observable<Device> createDevice(String name, Class<? extends Device> deviceType) {
        return Observable.just(DevicesFactory.createDevice(name, deviceType))
    }

}
