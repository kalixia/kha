package com.kalixia.ha.api

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.configuration.Configuration
import com.kalixia.ha.model.configuration.ConfigurationBuilder
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.devices.DeviceFactory
import com.kalixia.ha.model.devices.DeviceMetadata
import groovy.util.logging.Slf4j
import org.apache.commons.beanutils.DynaBean
import org.apache.commons.beanutils.WrapDynaBean
import rx.Observable

import static com.google.common.base.Preconditions.checkNotNull

@Slf4j(value = "LOGGER")
class DevicesServiceImpl implements DevicesService {
    final DevicesDao devicesDao

    DevicesServiceImpl(DevicesDao devicesDao) {
        this.devicesDao = devicesDao
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
    Observable<DeviceMetadata> findAllSupportedDevices() {
        return DeviceFactory.instance.findAllSupportedDevices()
    }

    @Override
    Device create(User owner, String name, String type) {
        return new DeviceBuilder()
            .ofType(type)
            .withName(name)
            .withOwner(owner)
            .build()
    }

    @Override
    Device configure(UUID id, Map configurationData) {
        Device device = findDeviceById(id)
        if (device == null)
            return device
        Configuration configuration = mergeWithConfigurationData(device, configurationData)
        // save it to the configuration file
        device.saveConfiguration(configuration)
        return device
    }

    private Configuration mergeWithConfigurationData(Device device, Map<String, Object> configurationData) {
        checkNotNull(device, "Device can't be null");
        Configuration configuration = device.configuration
        checkNotNull(configuration, "Device configuration can't be null")
        // merge with configuration data
        DynaBean wrapper = new WrapDynaBean(configuration);
        configurationData.each { key, value -> wrapper.set(key, value) }
        return configuration
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
