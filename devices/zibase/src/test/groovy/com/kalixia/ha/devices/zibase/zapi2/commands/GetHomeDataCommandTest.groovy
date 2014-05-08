package com.kalixia.ha.devices.zibase.zapi2.commands

import com.kalixia.ha.model.sensors.Sensor

class GetHomeDataCommandTest extends AbstractZibaseCommandTest {

    def "fetch home data from demo account"() {
        given:
        FetchTokenCommand tokenCmd = new FetchTokenCommand(configuration, httpClient, mapper)

        when:
        def token = tokenCmd.execute()

        then:
        token != ''

        when:
        GetHomeDataCommand homeDataCmd = new GetHomeDataCommand(token, configuration, httpClient, mapper)
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