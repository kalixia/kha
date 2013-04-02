package com.kalixia.ha.api

import com.kalixia.ha.model.sensors.DataPoint
import rx.Observable

import javax.inject.Inject

class SensorsServiceImpl implements SensorsService {
    @Inject SensorsDao dao

    @Override
    def Observable<DataPoint> getLastValue(UUID sensorID) {
        return Observable.just(dao.getLastValue(sensorID))
    }

    @Override
    def Observable<DataPoint> getLastHourData(UUID sensorID) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

}
