package com.kalixia.ha.api

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.model.devices.Device
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
    def Device findDeviceById(UUID id) {
        LOGGER.info("Searching for device with ID {}'", id)
        return devicesDao.findById(id)
    }

    @Override
    def Device findDeviceByName(String ownerUsername, String name) {
        LOGGER.info("Searching for device of user '{}' named '{}'", ownerUsername, name)
        return devicesDao.findByOwnerAndName(ownerUsername, name)
    }

    @Override
    def void saveDevice(Device device) {
        devicesDao.save(device)
    }

    @Override
    void deleteDevice(UUID id) {
        devicesDao.delete(id)
    }
}
