package com.kalixia.ha.dao.cassandra

import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.RGBLamp

class DevicesDaoTest extends AbstractCassandraDaoTest {

    def "create a user with one device and retreive the create device"() {
        given:
        def user = new User(username: 'johndoe', email: 'john@doe.com', firstName: 'John', lastName: 'Doe')
        def deviceId = new DeviceRK(user.getUsername(), 'my lamp')
        def device = new RGBLamp<DeviceRK>(deviceId, 'my lamp', user)

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
