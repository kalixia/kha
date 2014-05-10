package com.kalixia.ha.devices.zibase.zapi2.commands

import com.kalixia.ha.model.sensors.DataPoint
import org.joda.time.DateMidnight

class GetProbeDataCommandTest extends AbstractZibaseCommandTest {

    def "fetch probe data from main power probe from demo account"() {
        given:
        FetchTokenCommand tokenCmd = new FetchTokenCommand(configuration, httpClient, mapper)

        when:
        def token = tokenCmd.execute()

        then:
        token != ''

        when:
        GetProbeDataCommand probeDataCmd = new GetProbeDataCommand(
//                "WS131149",       // main power usage probe
                "OS439156737",  // temperature probe
                token,
                configuration, httpClient, mapper,
                DateMidnight.now())
        def List<DataPoint> dataPoints = probeDataCmd.execute()

        then:
        dataPoints != null
        !dataPoints.isEmpty()

        when:
        dataPoints.each { println it }

        then:
        1 == 1
    }

}