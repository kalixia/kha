package com.kalixia.ha.api

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceMetadata
import groovy.util.logging.Slf4j
import rx.Observable

import java.util.function.Consumer

import static com.google.common.base.Preconditions.checkNotNull

@Slf4j(value = "LOGGER")
class DevicesServiceImpl implements DevicesService {
    final DevicesDao devicesDao
    final ServiceLoader<DeviceMetadata> devicesMetadataLoader

    DevicesServiceImpl(DevicesDao devicesDao) {
        this.devicesDao = devicesDao
        devicesMetadataLoader = ServiceLoader.load(DeviceMetadata.class)
        if (LOGGER.isDebugEnabled()) {
            devicesMetadataLoader.forEach(new Consumer<DeviceMetadata>() {
                @Override
                void accept(DeviceMetadata deviceMetadata) {
                    LOGGER.debug("Found device metadata for '{}'", deviceMetadata.name)
                }
            })
        }
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
        LOGGER.info("Searching for all supported devices...")
        if (!devicesMetadataLoader.iterator().hasNext()) {
            LOGGER.warn("No device supported. Most likely your installation is messed up!")
            return Observable.empty()
        } else {
            return Observable.from(devicesMetadataLoader)
        }
    }

    @Override
    Device create(User owner, String name, String type) {
        checkNotNull(owner)
        checkNotNull(name)
        checkNotNull(type)
        if (type.isEmpty())
            throw new IllegalArgumentException("Missing device type")

        Device device
        Iterator<DeviceMetadata> supportedDevices = devicesMetadataLoader.iterator();
        for (DeviceMetadata metadata : supportedDevices) {
            if (metadata.type == type) {
                device = metadata.createDevice(owner, name)
                return device
            }
        }

        if (device == null)
            throw new IllegalArgumentException(String.format("Invalid device type '%s'", type))
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
