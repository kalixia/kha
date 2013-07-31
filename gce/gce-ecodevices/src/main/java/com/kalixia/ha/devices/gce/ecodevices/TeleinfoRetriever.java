package com.kalixia.ha.devices.gce.ecodevices;

import rx.Observable;

public interface TeleinfoRetriever {
    /**
     * Return an observable made of 2 values: one for HP and one for HC.
     * @return the two indexes
     */
    Observable<Long> retrieveIndexes(Teleinfo teleinfo, EcoDeviceConfiguration configuration);
}
