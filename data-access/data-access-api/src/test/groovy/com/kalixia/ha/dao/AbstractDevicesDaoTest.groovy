package com.kalixia.ha.dao

import com.kalixia.ha.devices.rgblamp.RGBLamp
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.sensors.SensorBuilder
import spock.lang.Specification

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
        def userFound = usersDao.findByUsername(user.getUsername())
        def deviceFound = devicesDao.findById(deviceId1)

        then:
        userFound.isPresent()
        user == userFound.get()
        deviceFound.isPresent()

        when:
        def device = deviceFound.get()

        then:
        device.name == 'my lamp'
        device.owner == user

        when:
        def devices = devicesDao.findAllDevicesOfUser(user.username).toList().toBlocking().single()

        then:
        devices.size() == 2
        devices.any { d -> d.name == 'another lamp' && d.owner == user }

        when:
        userFound = usersDao.findByUsername(user.getUsername())
        deviceFound = devicesDao.findByOwnerAndName(user.username, device1.name)

        then:
        userFound.isPresent()
        user == userFound.get()
        deviceFound.isPresent()

        when:
        device = deviceFound.get()

        then:
        device.name == 'my lamp'
        device.owner == user
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
        def userFound = usersDao.findByUsername(user.getUsername())
        def deviceFound = devicesDao.findById(deviceId)

        then:
        userFound.isPresent()
        user == userFound.get()
        deviceFound.isPresent()

        when:
        def d = deviceFound.get()

        then:
        d.name == 'my lamp'
        d.owner == user
        d.sensors.size() == 1
        d.sensors[0].name == 'dummy'
        d.sensors[0].unit == SI.SECOND
    }

    def "test searching for a missing sensor"() {
        when:
        def deviceFound = devicesDao.findById(UUID.randomUUID())

        then:
        !deviceFound.isPresent()

        when:
        deviceFound = devicesDao.findByOwnerAndName("john", "doe")

        then:
        !deviceFound.isPresent()
    }

}
