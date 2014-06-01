package com.kalixia.ha.dao

import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.RGBLamp
import com.kalixia.ha.model.sensors.DataPoint
import com.kalixia.ha.model.sensors.Sensor
import spock.lang.Specification

import javax.measure.Measure
import javax.measure.quantity.Duration
import javax.measure.unit.SI
import javax.measure.unit.Unit

import static java.util.Collections.emptySet

abstract class AbstractDevicesDaoTest extends Specification {

    def abstract UsersDao getUsersDao()
    def abstract DevicesDao getDevicesDao()
    def abstract UUID createUUID()

    def "create a user with one device without sensors and retrieve the created device by its ID"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def deviceId1 = createUUID()
        def deviceId2 = createUUID()
        def device1 = new RGBLamp(deviceId1, 'my lamp', user)
        def device2 = new RGBLamp(deviceId2, 'another lamp', user)

        when:
        usersDao.save(user)
        devicesDao.save(device1)
        devicesDao.save(device2)
        def deviceFound = devicesDao.findById(deviceId1)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound.name == 'my lamp'
        deviceFound.owner == user

        when:
        def devices = devicesDao.findAllDevicesOfUser(user.username).toList().toBlockingObservable().single()

        then:
        devices.size() == 2
        devices.any { device -> device.name == 'another lamp' && device.owner == user }

        when:
        deviceFound = devicesDao.findByOwnerAndName(user.username, device1.name)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound.name == 'my lamp'
        deviceFound.owner == user
    }

    def "create a user with one device with one sensor and retrieve the created device"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def deviceId = createUUID()
        def device = new RGBLamp(deviceId, 'my lamp', user)
        device.addSensor(new Sensor<Duration>() {
            @Override
            String getName() {
                return "dummy"
            }

            @Override
            Unit getUnit() {
                return SI.SECOND
            }

            @Override
            DataPoint<Duration> getLastValue() {
                return new DataPoint<Duration>(Measure.valueOf(123, SI.SECOND));
            }
        })

        when:
        usersDao.save(user)
        devicesDao.save(device)
        def deviceFound = devicesDao.findById(deviceId)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound.name == 'my lamp'
        deviceFound.owner == user
        deviceFound.sensors.size() == 1
        deviceFound.sensors[0].name == 'dummy'
        deviceFound.sensors[0].unit == SI.SECOND
    }

}
