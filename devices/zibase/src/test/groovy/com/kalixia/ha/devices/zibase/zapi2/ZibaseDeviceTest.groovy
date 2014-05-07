package com.kalixia.ha.devices.zibase.zapi2

import com.kalixia.ha.model.User
import spock.lang.Specification

class ZibaseDeviceTest extends Specification {
    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
    }

    def "test configuration"() {
        when: "creating a test Zibase"
        ZibaseDevice device = new ZibaseDevice(UUID.randomUUID(), "test device", new User("test"))

        then: "the configuration to be properly loaded"
        device.configuration != null
        device.configuration.url == 'https://zibase.net/api/get/ZAPI.php'
        device.configuration.authentication.username == 'demo'
        device.configuration.authentication.password == 'demo'
    }

}
