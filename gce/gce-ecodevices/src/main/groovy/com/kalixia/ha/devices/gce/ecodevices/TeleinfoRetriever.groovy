package com.kalixia.ha.devices.gce.ecodevices

import rx.Observable

import javax.measure.Measurable
import javax.measure.quantity.Quantity

/**
 * Retrieve @{link TeleinfoSensor} data.
 */
public interface TeleinfoRetriever {
    /**
     * Return an observable made of 3 values: instant power (W), index for HP (W/h) and index for HC (W/h).
     * @return the instant power and the two indexes
     */
    Observable<Measurable<Quantity>> retrieveIndexes(TeleinfoSensor teleinfoSensor, EcoDeviceConfiguration configuration)
}