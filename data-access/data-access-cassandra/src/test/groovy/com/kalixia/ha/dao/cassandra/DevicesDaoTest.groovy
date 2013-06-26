package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.model.User

import com.kalixia.ha.model.devices.RGBLamp
import com.kalixia.ha.model.sensors.Sensor
import com.netflix.astyanax.util.TimeUUIDUtils

import javax.measure.unit.SI
import javax.measure.unit.Unit

class DevicesDaoTest extends AbstractCassandraDaoTest {

    def "create a user with one device without sensors and retrieve the created device by its ID"() {
        given:
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')
        def deviceId1 = TimeUUIDUtils.uniqueTimeUUIDinMicros
        def deviceId2 = TimeUUIDUtils.uniqueTimeUUIDinMicros
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
        devices[0].name == 'another lamp'  // this is inverted from insertion order as the name of the device matters!
        devices[0].owner == user

        when:
        deviceFound = devicesDao.findByOwnerAndName(user.username, device1.name)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound.name == 'my lamp'
        deviceFound.owner == user
    }

    def "create a user with one device with one sensor and retrieve the created device"() {
        given:
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')
        def deviceId = TimeUUIDUtils.uniqueTimeUUIDinMicros
        def device = new RGBLamp(deviceId, 'my lamp', user)
        device.addSensor(new Sensor() {
            @Override
            String getName() {
                return "dummy"
            }

            @Override
            Unit getUnit() {
                return SI.SECOND
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
