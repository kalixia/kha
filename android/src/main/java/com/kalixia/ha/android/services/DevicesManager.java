package com.kalixia.ha.android.services;

import com.googlecode.androidannotations.annotations.EBean;
import com.kalixia.ha.model.Capability;
import com.kalixia.ha.model.Device;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@EBean
public class DevicesManager {

    public List<Device> findAllDevices() {
        // TODO: call REST API instead!
        List<Capability> capabilities = Collections.emptyList();
        return Arrays.asList(
                new Device(UUID.randomUUID(), "Lamp 1", capabilities),
                new Device(UUID.randomUUID(), "Lamp 2", capabilities)
        );
    }

}
