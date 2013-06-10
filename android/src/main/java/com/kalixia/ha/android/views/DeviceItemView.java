package com.kalixia.ha.android.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;
import com.kalixia.ha.android.R;
import com.kalixia.ha.model.devices.Device;

@EViewGroup(R.layout.device_list_item)
public class DeviceItemView extends LinearLayout {
    @ViewById(R.id.device_list_item_device_id)
    TextView tfDeviceID;

    @ViewById(R.id.device_list_item_device_name)
    TextView tfDeviceName;

    public DeviceItemView(Context context) {
        super(context);
    }

    public void bind(Device device) {
        tfDeviceID.setText(device.getId().toString());
        tfDeviceName.setText(device.getName());
    }
}
