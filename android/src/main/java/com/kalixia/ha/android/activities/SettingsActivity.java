package com.kalixia.ha.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.kalixia.ha.android.fragments.SettingsFragment_;

@EActivity
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getActionBar().setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment_())
                .commit();
    }

    @OptionsItem(android.R.id.home)
    void goHome() {
        Intent intent = new Intent(this, DevicesActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
