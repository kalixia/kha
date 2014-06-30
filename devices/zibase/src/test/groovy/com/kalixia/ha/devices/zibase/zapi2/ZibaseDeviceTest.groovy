package com.kalixia.ha.devices.zibase.zapi2

import com.kalixia.ha.model.User
import com.kalixia.ha.model.devices.DeviceBuilder
import com.kalixia.ha.model.security.Role
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

class ZibaseDeviceTest extends Specification {
    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test configuration"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, newHashSet())

        when: "creating a test Zibase"
        ZibaseDevice device = new DeviceBuilder()
                .ofType(ZibaseDevice.TYPE)
                .withName('test device')
                .withOwner(user)
                .build()
        def configuration = device.configuration

        then: "the configuration to be properly loaded"
        configuration != null
        configuration.zibaseID == 'ZiBASE005748'
        configuration.authentication.username == 'demo'
        configuration.authentication.password == 'demo'
        configuration.url == 'https://zibase.net/api/get/ZAPI.php'
    }

}
