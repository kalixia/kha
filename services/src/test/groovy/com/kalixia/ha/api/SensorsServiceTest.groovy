package com.kalixia.ha.api

import com.kalixia.ha.dao.SensorsDao
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.RGBLamp
import com.kalixia.ha.model.sensors.DataPoint
import org.joda.time.Instant
import spock.lang.Specification

class SensorsServiceTest extends Specification {

    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test user with no device having one sensor"() {
        given: "a user who has one device with one sensor"
        def dao = Mock(SensorsDao)
        def service = new SensorsServiceImpl(dao)
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')
        def deviceAndSensor = new RGBLamp(UUID.randomUUID(), 'my lamp', user)
        dao.getLastValue(deviceAndSensor.id) >> new DataPoint(123L, Instant.now())

        when: "requesting last sensor value"
        def observable = service.getLastValue(deviceAndSensor.id)
        def DataPoint<Long> dataPoint
        observable.subscribe({ dataPoint = it })

        then: "expect to get it"
        dataPoint != null
        dataPoint.value == 123L
    }

}
