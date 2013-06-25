package com.kalixia.ha.android.services;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.api.Scope;
import com.kalixia.ha.model.Color;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceID;
import com.kalixia.ha.model.devices.RGBLamp;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@EBean(scope = Scope.Singleton)
public class DevicesManager {

    public List<? extends Device> findAllDevices() {
        // TODO: call REST API instead!
        User owner = new User("johndoe");
        RGBLamp lamp1 = new RGBLamp(new DeviceID("johndoe", "Lamp 1"), "Lamp 1", owner);
        RGBLamp lamp2 = new RGBLamp(new DeviceID("johndoe", "Lamp 2"), "Lamp 2", owner);
        RGBLamp lamp3 = new RGBLamp(new DeviceID("johndoe", "Lamp 3"), "Lamp 3", owner);
        RGBLamp lamp4 = new RGBLamp(new DeviceID("johndoe", "Lamp 4"), "Lamp 4", owner);
        RGBLamp lamp5 = new RGBLamp(new DeviceID("johndoe", "Lamp 5"), "Lamp 5", owner);

        // set random colors and switch both lamps on
        lamp1.setColor(new Color(0f, 1f, 1f));
        lamp2.setColor(new Color(60f, 1f, 1f));
        lamp3.setColor(new Color(120f, 1f, 1f));
        lamp4.setColor(new Color(180f, 1f, 1f));
        lamp5.setColor(new Color(240f, 1f, 1f));
        lamp1.on();
        lamp2.on();
        lamp3.on();
        lamp4.on();
        lamp5.on();

        return Arrays.asList(lamp1, lamp2, lamp3, lamp4, lamp5);
    }

}
