package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.User
import com.kalixia.ha.model.security.Role
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

class EcoDeviceTest extends Specification {
    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test configuration"() {
        given:
        def user = new User('johndoe', 'missingpwd', 'john@doe.com', 'John', 'Doe', [Role.USER] as Set<Role>, newHashSet())

        when: "creating a test EcoDevice"
        EcoDevice device = new EcoDevice(UUID.randomUUID(), "test device", user)

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
