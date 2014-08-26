package com.kalixia.ha.api

import com.kalixia.ha.dao.SensorsDao
import com.kalixia.ha.devices.rgblamp.RGBLamp
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.sensors.DataPoint
import spock.lang.Specification

import javax.measure.Measure
import javax.measure.quantity.LuminousFlux

import static java.util.Collections.emptySet

class SensorsServiceTest extends Specification {

    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test user with no device having one sensor"() {
        given: "a user who has one device with one sensor"
        def dao = Mock(SensorsDao)
        def service = new SensorsServiceImpl(dao)
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def deviceAndSensor = new DeviceBuilder().ofType(RGBLamp.TYPE).withName('my lamp').withOwner(user).build()
        dao.getLastValue(deviceAndSensor.id) >> new DataPoint<LuminousFlux>(Measure.valueOf(3, LuminousFlux.UNIT))

        when: "requesting last sensor value"
        def observable = service.getLastValue(deviceAndSensor.id)
        def DataPoint<LuminousFlux> dataPoint = observable.toBlockingObservable().iterator.next()

        then: "expect to get it"
        dataPoint != null
        Measure.valueOf(3, LuminousFlux.UNIT).equals(dataPoint.value)
    }

}
