package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.configuration.ConfigurationBuilder
import com.kalixia.ha.model.quantity.WattsPerHour
import com.netflix.hystrix.contrib.yammermetricspublisher.HystrixYammerMetricsPublisher
import com.netflix.hystrix.strategy.HystrixPlugins
import groovy.util.logging.Slf4j
import io.reactivex.netty.RxNetty
import spock.lang.Shared
import spock.lang.Specification

import javax.measure.Measurable
import javax.measure.Measure
import javax.measure.unit.SI

import static TeleinfoSensorSlot.TELEINFO1
import static TeleinfoSensorSlot.TELEINFO2

@Slf4j("LOGGER")
class TeleinfoSensorRetrieverTest extends Specification {
    @Shared def teleinfoRetriever
    @Shared EcoDeviceConfiguration ecoDeviceConfiguration
    @Shared TeleinfoSensor teleinfo1
    @Shared TeleinfoSensor teleinfo2
    @Shared MockedEchoDeviceServer fakeServer

    def setupSpec() {
        fakeServer = new MockedEchoDeviceServer()
        fakeServer.start()

        ecoDeviceConfiguration = ConfigurationBuilder.loadConfiguration("eco-device", "gce-eco-device",
                EcoDeviceConfiguration.class)
        teleinfo1 = new TeleinfoSensor(ecoDeviceConfiguration.power1.name, TELEINFO1)
        teleinfo2 = new TeleinfoSensor(ecoDeviceConfiguration.power1.name, TELEINFO2)

        def httpClient = RxNetty.createHttpClient(ecoDeviceConfiguration.getHost(), ecoDeviceConfiguration.getPort())
        teleinfoRetriever = new EcoDeviceTeleinfoRetriever(httpClient)

        HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixYammerMetricsPublisher())
    }

    def cleanupSpec() {
        fakeServer.stop()
    }

    def "test retrieval of teleinfo counter"() {
        when: "requesting for enabled teleinfoSensor1"
        def iterator = teleinfoRetriever.retrieveIndexes(teleinfo1, ecoDeviceConfiguration).toBlocking().iterator
        def instant = iterator.next()
        def hp = iterator.next()
        def hc = iterator.next()

        then: "expect to get non-zero values"
        instant.doubleValue(SI.WATT) > 0
        hp.doubleValue(WattsPerHour.UNIT) > 0
        hc.doubleValue(WattsPerHour.UNIT) > 0
    }

    def "test retrieval of disabled teleinfo counter"() {
        when: "requesting for disabled teleinfoSensor2"
        def iterator = teleinfoRetriever.retrieveIndexes(teleinfo2, ecoDeviceConfiguration).toBlocking().iterator
        def instant = iterator.next()
        def hp = iterator.next()
        def hc = iterator.next()

        then: "expect zero values"
        instant == Measure.valueOf(0L, SI.WATT)
        hp == Measure.valueOf(0L, WattsPerHour.UNIT)
        hc == Measure.valueOf(0L, WattsPerHour.UNIT)
    }

    def "test retrieval of teleinfo counter with no energy meter connected"() {
        when: "requesting for enabled teleinfoSensor2 with no energy meter connected"
        //noinspection GroovyAccessibility
        ecoDeviceConfiguration.power2.enabled = true
        def iterator = teleinfoRetriever.retrieveIndexes(teleinfo2, ecoDeviceConfiguration).toBlocking().iterator
        def instant = iterator.next()
        def hp = iterator.next()
        def hc = iterator.next()

        then: "expect to get zero values"
        instant.doubleValue(SI.WATT) == 0
        hp.doubleValue(WattsPerHour.UNIT) == 0
        hc.doubleValue(WattsPerHour.UNIT) == 0
    }

}