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
        def configuration = device.configuration

        then: "the configuration to be properly loaded"
        configuration != null
        configuration.zibaseID == 'ZiBASE005748'
        configuration.authentication.username == 'demo'
        configuration.authentication.password == 'demo'
        configuration.url == 'https://zibase.net/api/get/ZAPI.php'
    }

}
