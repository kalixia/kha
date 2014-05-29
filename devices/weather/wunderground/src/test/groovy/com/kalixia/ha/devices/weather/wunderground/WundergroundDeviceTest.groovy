package com.kalixia.ha.devices.weather.wunderground

import com.kalixia.ha.model.User
import spock.lang.Specification

class WundergroundDeviceTest extends Specification {
    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test configuration"() {
        when: "creating a test Wunderground virtual device"
        WundergroundDevice device = new WundergroundDevice(UUID.randomUUID(), "test device", new User("test"))
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
