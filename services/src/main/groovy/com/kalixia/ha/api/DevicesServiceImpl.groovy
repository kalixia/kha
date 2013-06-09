package com.kalixia.ha.api

import com.kalixia.ha.model.Device
import groovy.util.logging.Slf4j
import rx.Observable
import rx.Subscription

@Slf4j(value = "LOGGER")
class DevicesServiceImpl implements DevicesService {
    final DevicesDao dao

    DevicesServiceImpl(DevicesDao dao) {
        this.dao = dao;
    }

    @Override
    def Observable<? extends Device> findAllDevicesOfUser(String username) {
        LOGGER.info("Searching for all devices of user {}...", username)
        return Observable.create({ observer ->
            try {
//                observer.onNext(devices[0])
//                observer.onNext(devices[1])
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
        // TODO: return real value from datastore
    }

    @Override
    def Observable<Device> findDeviceById(UUID id) {
        LOGGER.info("Searching for device {}", id)
        return Observable.just(dao.findById(id))
    }

    @Override
    def void saveDevice(Device device) {
        dao.save(device)
    }

}
