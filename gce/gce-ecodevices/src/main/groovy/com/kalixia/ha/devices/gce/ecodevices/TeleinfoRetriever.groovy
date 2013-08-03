package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.quantity.WattsPerHour
import rx.Observable

import javax.measure.Measurable

/**
 * Retrieve @{link TeleinfoSensor} data.
 */
public interface TeleinfoRetriever {
    /**
     * Return an observable made of 2 values: one for HP and one for HC.
     * @return the two indexes
     */
    Observable<Measurable<WattsPerHour>> retrieveIndexes(TeleinfoSensor teleinfoSensor, EcoDeviceConfiguration configuration)
}