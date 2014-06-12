package com.kalixia.ha.devices.weather.wunderground

import com.kalixia.ha.model.User
import com.kalixia.ha.model.security.Role
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

class WundergroundDeviceTest extends Specification {
    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test configuration"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, newHashSet())

        when: "creating a test Wunderground virtual device"
        WundergroundDevice device = new WundergroundDevice(UUID.randomUUID(), "test device", user)
        def configuration = device.configuration

        then: "the configuration to be properly loaded"
        configuration != null
        configuration.apiKey == '8ad077feebe28cb8'
        configuration.station == 'IMOUYSUR2'
        configuration.host == 'api.wunderground.com'
        configuration.port == 80
        configuration.polling.pollingInterval == '5mn'
    }

}
