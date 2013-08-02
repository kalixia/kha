package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.model.configuration.ConfigurationBuilder
import com.kalixia.ha.model.quantity.WattsPerHour
import com.netflix.hystrix.contrib.yammermetricspublisher.HystrixYammerMetricsPublisher
import com.netflix.hystrix.strategy.HystrixPlugins
import groovy.util.logging.Slf4j
import spock.lang.Shared
import spock.lang.Specification

import javax.measure.Measurable
import javax.measure.Measure

import static TeleinfoSensorSlot.TELEINFO1
import static TeleinfoSensorSlot.TELEINFO2

@Slf4j("LOGGER")
class TeleinfoSensorRetrieverTest extends Specification {
    @Shared def teleinfoRetriever = new EcoDeviceTeleinfoRetriever()
    @Shared EcoDeviceConfiguration ecoDeviceConfiguration
    @Shared def teleinfo1 = new TeleinfoSensor(TELEINFO1)
    @Shared def teleinfo2 = new TeleinfoSensor(TELEINFO2)
    @Shared MockedEchoDeviceServer fakeServer

    def setupSpec() {
        fakeServer = new MockedEchoDeviceServer()
        fakeServer.start()

        ecoDeviceConfiguration = ConfigurationBuilder.loadConfiguration("eco-device", "gce-eco-device",
                EcoDeviceConfiguration.class)
        HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixYammerMetricsPublisher())
    }

    def cleanupSpec() {
        fakeServer.stop()
    }

    def "test measures"() {
        when:
        Measurable<WattsPerHour> measure = Measure.valueOf(0L, WattsPerHour.UNIT)

        then:
        measure.longValue(WattsPerHour.UNIT) == 0L
    }

    def "test retrieval of teleinfo counter"() {
        when: "requesting for enabled teleinfoSensor1"
        def iterator = teleinfoRetriever.retrieveIndexes(teleinfo1, ecoDeviceConfiguration)
                .toBlockingObservable().iterator
        def hp = iterator.next()
        def hc = iterator.next()

        then: "expect to get non-zero values"
        hp.doubleValue(WattsPerHour.UNIT) > 0
        hc.doubleValue(WattsPerHour.UNIT) > 0
    }

    def "test retrieval of disabled teleinfo counter"() {
        when: "requesting for disabled teleinfoSensor2"
        def iterator = teleinfoRetriever.retrieveIndexes(teleinfo2, ecoDeviceConfiguration)
                .toBlockingObservable().iterator
        def hp = iterator.next()
        def hc = iterator.next()

        then: "expect zero values"
        hp == Measure.valueOf(0L, WattsPerHour.UNIT)
        hc == Measure.valueOf(0L, WattsPerHour.UNIT)
    }

    def "test retrieval of teleinfo counter with no energy meter connected"() {
        when: "requesting for enabled teleinfoSensor2 with no energy meter connected"
        ecoDeviceConfiguration.power2SensorConfiguration.enabled = true
        def iterator = teleinfoRetriever.retrieveIndexes(teleinfo2, ecoDeviceConfiguration)
                        .toBlockingObservable().iterator
        def hp = iterator.next()
        def hc = iterator.next()

        then: "expect to get zero values"
        hp.doubleValue(WattsPerHour.UNIT) == 0
        hc.doubleValue(WattsPerHour.UNIT) == 0
    }

}