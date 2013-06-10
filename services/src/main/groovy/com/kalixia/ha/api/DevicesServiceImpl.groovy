package com.kalixia.ha.api

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceID
import groovy.util.logging.Slf4j
import rx.Observable

@Slf4j(value = "LOGGER")
class DevicesServiceImpl implements DevicesService {
    final DevicesDao devicesDao

    DevicesServiceImpl(DevicesDao devicesDao) {
        this.devicesDao = devicesDao;
    }

    @Override
    def Observable<? extends Device> findAllDevicesOfUser(String username) {
        LOGGER.info("Searching for all devices of user '{}'...", username)
        return devicesDao.findAllDevicesOfUser(username)
    }

    @Override
    def Device findDeviceById(DeviceID id) {
        LOGGER.info("Searching for device '{}' of user '{}'", id.getDeviceName(), id.getOwner())
        return devicesDao.findById(id)
    }

    @Override
    def void saveDevice(Device device) {
        devicesDao.save(device)
    }

    @Override
    void deleteDevice(DeviceID id) {
        devicesDao.delete(id)
    }
}
