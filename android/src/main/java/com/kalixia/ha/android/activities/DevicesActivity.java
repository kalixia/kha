package com.kalixia.ha.android.activities;

import android.app.Activity;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.kalixia.ha.android.R;
import com.kalixia.ha.android.fragments.DevicesListFragment;

@EActivity
@OptionsMenu(R.menu.main)
public class DevicesActivity extends Activity {
    @FragmentById(R.id.devicesListFragment)
    DevicesListFragment devicesListFragment;
}
