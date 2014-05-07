package com.kalixia.ha.devices.zibase.zapi2;

import com.kalixia.ha.model.User;
import com.kalixia.ha.model.capabilities.Counter;
import com.kalixia.ha.model.devices.AbstractDevice;
import com.kalixia.ha.model.devices.PullBasedDevice;

import java.util.UUID;

public class ZibaseDevice extends AbstractDevice<ZibaseDeviceConfiguration> implements PullBasedDevice {

    public ZibaseDevice(UUID id, String name, User owner) {
        super(id, name, owner, Counter.class);
    }

    @Override
    public void init(ZibaseDeviceConfiguration configuration) {
        // TODO: fetch Zibase devices after init
    }

    @Override
    public void fetchSensorsData() {
        // TODO
    }

    @Override
    protected String getConfigurationFilename() {
        return "zibase-device";
    }

    @Override
    protected Class<ZibaseDeviceConfiguration> getConfigurationClass() {
        return ZibaseDeviceConfiguration.class;
    }

}
