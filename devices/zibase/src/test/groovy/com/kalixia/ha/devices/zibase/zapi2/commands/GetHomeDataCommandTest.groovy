package com.kalixia.ha.devices.zibase.zapi2.commands

import com.kalixia.ha.devices.zibase.zapi2.ZibaseDevice
import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.security.Role
import com.kalixia.ha.model.sensors.Sensor

import static java.util.Collections.emptySet

class GetHomeDataCommandTest extends AbstractZibaseCommandTest {

    def "fetch home data from demo account"() {
        given:
        FetchTokenCommand tokenCmd = new FetchTokenCommand(configuration, httpClient, mapper)
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, emptySet())
        def ZibaseDevice device = new DeviceBuilder()
                .ofType(ZibaseDevice.TYPE)
                .withName('my-zibase')
                .withOwner(user)
                .build()

        when:
        def token = tokenCmd.execute()

        then:
        token != ''

        when:
        GetHomeDataCommand homeDataCmd = new GetHomeDataCommand(token, device, httpClient, mapper)
        def List<Sensor> sensors = homeDataCmd.execute()

        then:
        sensors != null
        !sensors.isEmpty()

        when:
        sensors.each { println it }

        then:
        1 == 1
    }

}