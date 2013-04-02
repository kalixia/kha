package com.kalixia.ha.api;

import com.kalixia.ha.model.sensors.DataPoint;
import rx.Observable;

import java.util.UUID;

public interface SensorsService {

    /**
     * Return the last know {@link DataPoint} of the sensor identified by <tt>sensorID</tt>.
     * @param sensorID the ID of the sensor for which data points should be retrieved
     * @return the last datapoint
     */
    Observable<DataPoint> getLastValue(UUID sensorID);

    /**
     * Return all data points from the last 60 minutes.
     * Data points are ordered and the most recent ones are given first.
     * @param sensorID the ID of the sensor for which data points should be retrieved
     * @return the data points
     */
    Observable<DataPoint> getLastHourData(UUID sensorID);

}
