package com.kalixia.ha.model.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.ServiceLoader;

public class DeviceFactory {
    private final ServiceLoader<DeviceMetadata> devicesMetadataLoader;
    private static DeviceFactory ourInstance = new DeviceFactory();
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceFactory.class);

    public DeviceFactory() {
        devicesMetadataLoader = ServiceLoader.load(DeviceMetadata.class);
//        if (LOGGER.isDebugEnabled()) {
//            devicesMetadataLoader.forEach(deviceMetadata ->
//                    LOGGER.debug("Found device metadata for '{}'", deviceMetadata.getName())
//            );
//        }
    }

    public Observable<DeviceMetadata> findAllSupportedDevices() {
        LOGGER.info("Searching for all supported devices...");
        if (!devicesMetadataLoader.iterator().hasNext()) {
            LOGGER.warn("No device supported. Most likely your installation is messed up!");
            return Observable.empty();
        } else {
            return Observable.from(devicesMetadataLoader);
        }
    }

    public static DeviceFactory getInstance() {
        return ourInstance;
    }

}
