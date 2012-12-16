package com.kalixia.ha.android.fragments;

import android.app.Fragment;
import android.widget.GridView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.kalixia.ha.android.R;
import com.kalixia.ha.android.adapters.DevicesListAdapter;

@EFragment(R.layout.devices_grid_list_fragment)
public class DevicesGridListFragment extends Fragment {

    @ViewById(R.id.devicesGridView)
    GridView devicesGridView;

    @Bean
    DevicesListAdapter adapter;

    @AfterViews
    void bindAdapter() {
        devicesGridView.setAdapter(adapter);
    }

    public void reload() {
        adapter.reload();
    }

}
