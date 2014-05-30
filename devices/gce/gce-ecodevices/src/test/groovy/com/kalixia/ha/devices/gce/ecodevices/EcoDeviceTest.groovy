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
        device.configuration.host == 'localhost'
        device.configuration.port == 12345
        device.configuration.authentication.username == 'admin'
        device.configuration.authentication.password == 'test'
        device.configuration.power1.name == 'Téléinfo 1'
        device.configuration.power1.enabled
        device.configuration.power2.name == 'Téléinfo 2'
        !device.configuration.power2.enabled
        device.configuration.counter1.name == 'Water'
        !device.configuration.counter1.enabled
        device.configuration.counter2.name == 'Gas'
        !device.configuration.counter2.enabled
        device.configuration.polling.pollingInterval == '15s'
    }

}
