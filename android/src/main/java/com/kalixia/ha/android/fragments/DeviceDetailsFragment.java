package com.kalixia.ha.android.fragments;

import android.app.Fragment;
import android.widget.TextView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.ViewById;
import com.kalixia.ha.android.R;

import java.util.UUID;

@EFragment(R.layout.device_details_fragment)
public class DeviceDetailsFragment extends Fragment {
    @FragmentArg
    UUID deviceId;

    @ViewById(R.id.device_details_device_id)
    TextView tfDeviceID;

    @AfterViews
    void initData() {
        if (deviceId != null) {
            tfDeviceID.setText(deviceId.toString());
        } else {
            tfDeviceID.setText("???");
        }
    }
}
