package com.kalixia.ha.android.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.kalixia.ha.android.services.DevicesManager;
import com.kalixia.ha.android.views.DeviceItemView;
import com.kalixia.ha.android.views.DeviceItemView_;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.capabilities.Light;

import java.util.List;

@EBean
public class DevicesListAdapter extends BaseAdapter {
    private List<? extends Device> devices;

    @Bean
    DevicesManager devicesManager;

    @RootContext
    Context context;

    @AfterInject
    void initAdapter() {
        reload();
    }

    public void reload() {
        devices = devicesManager.findAllDevices();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceItemView deviceItemView;
        if (convertView == null) {
            deviceItemView = DeviceItemView_.build(context);
        } else {
            deviceItemView = (DeviceItemView) convertView;
        }
        Device device = getItem(position);
        deviceItemView.bind(device);

        if (device.hasCapability(Light.class)) {
            com.kalixia.ha.model.Color color = ((Light) device).getColor();
            deviceItemView.setBackgroundColor(
                    Color.HSVToColor(new float[]{color.getHue(), color.getSaturation(), color.getValue()}));
        }

        return deviceItemView;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Device getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
