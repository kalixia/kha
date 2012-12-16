package com.kalixia.ha.android.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.googlecode.androidannotations.annotations.EFragment;
import com.kalixia.ha.android.R;

@EFragment
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
