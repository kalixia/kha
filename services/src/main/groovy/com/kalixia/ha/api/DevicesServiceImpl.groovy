package com.kalixia.ha.api

import com.kalixia.ha.api.cassandra.DeviceRK
import com.kalixia.ha.model.Device
import com.kalixia.ha.model.User
import groovy.util.logging.Slf4j
import rx.Observable
import rx.Subscription

@Slf4j(value = "LOGGER")
class DevicesServiceImpl implements DevicesService {
    final DevicesDao devicesDao

    DevicesServiceImpl(DevicesDao devicesDao) {
        this.devicesDao = devicesDao;
    }

    @Override
    def Observable<? extends Device> findAllDevicesOfUser(String username) {
        LOGGER.info("Searching for all devices of user {}...", username)
        return Observable.from(devicesDao.findAllDevicesOfUser(username))
        /*
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
        */
    }

    @Override
    def Observable<Device> findDeviceById(UUID id) {
        LOGGER.info("Searching for device {}", id)
        return Observable.just(devicesDao.findById(id))
    }

    @Override
    def void saveDevice(Device device) {
        devicesDao.save(device)
    }

}
