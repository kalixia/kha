package com.kalixia.ha.api

import com.kalixia.ha.model.sensors.DataPoint
import groovy.util.logging.Slf4j
import rx.Observable

import javax.inject.Inject

@Slf4j("LOGGER")
class SensorsServiceImpl implements SensorsService {
    final SensorsDao dao

    SensorsServiceImpl(SensorsDao dao) {
        this.dao = dao
    }

    @Override
    def Observable<DataPoint> getLastValue(UUID sensorID) {
        return Observable.just(dao.getLastValue(sensorID))
    }

    @Override
    def Observable<DataPoint> getLastHourData(UUID sensorID) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

}
