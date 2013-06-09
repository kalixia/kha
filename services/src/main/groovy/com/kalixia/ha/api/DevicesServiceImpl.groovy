package com.kalixia.ha.api

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.dao.cassandra.DeviceRK
import com.kalixia.ha.model.Device
import groovy.util.logging.Slf4j
import rx.Observable

@Slf4j(value = "LOGGER")
class DevicesServiceImpl implements DevicesService<DeviceRK> {
    final DevicesDao<DeviceRK> devicesDao

    DevicesServiceImpl(DevicesDao devicesDao) {
        this.devicesDao = devicesDao;
    }

    @Override
    def Observable<? extends Device<DeviceRK>> findAllDevicesOfUser(String username) {
        LOGGER.info("Searching for all devices of user {}...", username)
        return devicesDao.findAllDevicesOfUser(username)
    }

    @Override
    def Observable<Device<DeviceRK>> findDeviceById(DeviceRK id) {
        LOGGER.info("Searching for device {}", id)
        return Observable.just(devicesDao.findById(id))
    }

    @Override
    def void saveDevice(Device<DeviceRK> device) {
        devicesDao.save(device)
    }

}
