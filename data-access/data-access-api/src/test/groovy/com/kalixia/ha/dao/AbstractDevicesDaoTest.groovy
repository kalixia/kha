package com.kalixia.ha.dao

import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.User
import com.kalixia.ha.devices.rgblamp.RGBLamp
import com.kalixia.ha.model.sensors.Sensor
import com.kalixia.ha.model.sensors.SensorBuilder
import spock.lang.Specification

import javax.measure.quantity.Duration
import javax.measure.unit.SI

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
        def device1 = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withID(deviceId1)
                .withName('my lamp')
                .withOwner(user)
                .build()
        def device2 = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withID(deviceId2)
                .withName('another lamp')
                .withOwner(user)
                .build()

        when:
        usersDao.save(user)
        devicesDao.save(device1)
        devicesDao.save(device2)
        def deviceFound = devicesDao.findById(deviceId1)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound != null
        deviceFound.name == 'my lamp'
        deviceFound.owner == user

        when:
        def devices = devicesDao.findAllDevicesOfUser(user.username).toList().toBlocking().single()

        then:
        devices.size() == 2
        devices.any { device -> device.name == 'another lamp' && device.owner == user }

        when:
        deviceFound = devicesDao.findByOwnerAndName(user.username, device1.name)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound != null
        deviceFound.name == 'my lamp'
        deviceFound.owner == user
    }

    def "create a user with one device with one sensor and retrieve the created device"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def deviceId = createUUID()
        def device = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withID(deviceId)
                .withName('my lamp')
                .withOwner(user)
                .build()

        def sensor = new SensorBuilder()
                .forDevice(device)
                .ofType("test-duration")
                .withName('dummy')
                .withUnit(SI.SECOND)
                .build()

        device.addSensor(sensor)

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

    def "test searching for a missing sensor"() {
        when:
        Device found = devicesDao.findById(UUID.randomUUID())

        then:
        found == null

        when:
        found = devicesDao.findByOwnerAndName("john", "doe")

        then:
        found == null
    }

}
