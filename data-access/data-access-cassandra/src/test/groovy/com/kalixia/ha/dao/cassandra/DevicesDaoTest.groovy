package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.DeviceID
import com.kalixia.ha.model.devices.RGBLamp

class DevicesDaoTest extends AbstractCassandraDaoTest {

    def "create a user with one device and retreive the create device"() {
        given:
        def user = new User('johndoe', 'john@doe.com', 'John', 'Doe')
        def deviceId = new DeviceID(user.getUsername(), 'my lamp')
        def device = new RGBLamp(deviceId, 'my lamp', user)

        when:
        usersDao.save(user)
        devicesDao.save(device)
        def deviceFound = devicesDao.findById(deviceId)

        then:
        user == usersDao.findByUsername(user.getUsername())
        deviceFound.name == 'my lamp'
        deviceFound.owner == user
    }

}
