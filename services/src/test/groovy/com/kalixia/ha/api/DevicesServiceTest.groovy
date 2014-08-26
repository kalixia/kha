package com.kalixia.ha.api

import com.kalixia.ha.dao.DevicesDao
import com.kalixia.ha.devices.rgblamp.RGBLamp
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.Device
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.security.Role
import rx.Observable
import spock.lang.Specification

import static java.util.Collections.emptySet

class DevicesServiceTest extends Specification {

    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test user with no device"() {
        given: "a user"
        def dao = Mock(DevicesDao)
        def service = new DevicesServiceImpl(dao)
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        dao.findAllDevicesOfUser(user.username) >> rx.Observable.create({ rx.Observer<Device> observer ->
            observer.onCompleted()
        })

        when: "who has no device"
        def observable = service.findAllDevicesOfUser(user.username)

        then: "expect not to find any device"
        observable != null
        !observable.toBlockingObservable().iterator.hasNext()
    }

    def "test user with two devices"() {
        given: "a user"
        def dao = Mock(DevicesDao)
        def service = new DevicesServiceImpl(dao)
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def device1 = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withName('lamp1')
                .withOwner(user)
                .build()
        def device2 = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withName('lamp2')
                .withOwner(user)
                .build()
        dao.findById(device1.id) >> device1
        dao.findById(device2.id) >> device2
        dao.findByOwnerAndName(user.username, device1.name) >> device1
        dao.findByOwnerAndName(user.username, device2.name) >> device2
        dao.findAllDevicesOfUser(user.username) >> rx.Observable.create({ rx.Observer<Device> observer ->
            observer.onNext(device1)
            observer.onNext(device2)
            observer.onCompleted()
        })

        when: "who has two devices"

        def observable = service.findAllDevicesOfUser(user.username)
        int foundDevices = 0
        observable.subscribe({ foundDevices++ })

        then: "expect to find those two devices by username"
        observable != null
        foundDevices == 2

        when: "searching for each device by ID"
        def foundDevice1 = service.findDeviceById(device1.id)
        def foundDevice2 = service.findDeviceById(device2.id)

        then: "expect to find them"
        foundDevice1 == device1
        foundDevice2 == device2

        when: "searching for each device by ID"
        foundDevice1 = service.findDeviceByName(user.username, device1.name)
        foundDevice2 = service.findDeviceByName(user.username, device2.name)

        then: "expect to find them"
        foundDevice1 == device1
        foundDevice2 == device2
    }

    def "test user with two devices for whom we delete the first one"() {
        given: "a user"
        def dao = Mock(DevicesDao)
        def service = new DevicesServiceImpl(dao)
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def device1 = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withName('lamp1')
                .withOwner(user)
                .build()
        def device2 = new DeviceBuilder()
                .ofType(RGBLamp.TYPE)
                .withName('lamp2')
                .withOwner(user)
                .build()

        when: "who has two devices"
        dao.findAllDevicesOfUser(user.username) >> rx.Observable.create({ rx.Observer<Device> observer ->
            observer.onNext(device2)
            observer.onCompleted()
        })

        and: "and delete the first one"
        service.deleteDevice(device1.id)
        def observable = service.findAllDevicesOfUser(user.username)
        def foundDevice
        observable.subscribe({ foundDevice = it })

        then: "expect to find only the second device"
        observable != null
        foundDevice != null
        foundDevice == device2
    }

    def "test searching for all supported devices"() {
        given:
        def dao = Mock(DevicesDao)
        def service = new DevicesServiceImpl(dao)

        when:
        Observable supportedDevices = service.findAllSupportedDevices()

        then:
        supportedDevices != null
        supportedDevices.count().toBlockingObservable().single() == 1
    }

    def "test creating an unsupported device"() {
        given:
        def dao = Mock(DevicesDao)
        def service = new DevicesServiceImpl(dao)
        def owner = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())

        when:
        service.create(owner, "my device", "unsupported-device")

        then:
        thrown(IllegalArgumentException)
    }

    def "test creating a RGB Lamp"() {
        given:
        def dao = Mock(DevicesDao)
        def service = new DevicesServiceImpl(dao)
        def owner = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())

        when:
        def device = service.create(owner, "my lamp", "rgb-lamp")
        dao.findById(device.id) >> device

        then:
        device != null
        device.id != null
        device.name == "my lamp"
        device.configuration != null
        device.configuration.intensity == 1.0

        when:
        service.configure(device.id, ['intensity': 0.5f])
//        device.reloadConfiguration()

        then:
        device != null
        device.configuration != null
        device.configuration.intensity == 0.5
    }

}
