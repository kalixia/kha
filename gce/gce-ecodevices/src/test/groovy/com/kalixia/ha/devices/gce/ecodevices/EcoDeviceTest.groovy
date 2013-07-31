package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.User
import spock.lang.Specification

class EcoDeviceTest extends Specification {
    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test configuration"() {
        when: "creating a test EcoDevice"
        EcoDevice device = new EcoDevice(UUID.randomUUID(), "test device", new User("test"))

        then: "the configuration to be properly loaded"
        device.configuration != null
        device.configuration.url == 'http://kalixia.com:81'
        device.configuration.authenticationConfiguration.username == 'admin'
        device.configuration.authenticationConfiguration.password == 'test'
        device.configuration.power1SensorConfiguration.enabled
        !device.configuration.power2SensorConfiguration.enabled
        !device.configuration.waterSensorConfiguration.enabled
        !device.configuration.gazSensorConfiguration.enabled
        device.configuration.polling.pollingInterval == '15s'
    }

}
