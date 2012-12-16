package com.kalixia.ha.android.fragments;

import android.app.Fragment;
import android.widget.ListView;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.kalixia.ha.android.R;
import com.kalixia.ha.android.adapters.DevicesListAdapter;

@EFragment(R.layout.devices_list_fragment)
public class DevicesListFragment extends Fragment {

    @ViewById(R.id.devicesListView)
    ListView devicesListView;

    @Bean
    DevicesListAdapter adapter;

    @AfterViews
    void bindAdapter() {
        devicesListView.setAdapter(adapter);
    }

    public void reload() {
        adapter.reload();
    }
}
