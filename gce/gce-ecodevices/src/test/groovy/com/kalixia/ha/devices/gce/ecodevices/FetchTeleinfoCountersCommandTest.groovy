package com.kalixia.ha.devices.gce.ecodevices

import com.kalixia.ha.devices.gce.ecodevices.commands.FetchTeleinfoCountersCommand
import com.kalixia.ha.model.configuration.ConfigurationBuilder
import groovy.util.logging.Slf4j
import rx.Observable
import rx.Observer
import spock.lang.Shared
import spock.lang.Specification

import static com.kalixia.ha.devices.gce.ecodevices.Teleinfo.TeleinfoName.TELEINFO1

@Slf4j("LOGGER")
class FetchTeleinfoCountersCommandTest extends Specification {
    @Shared
    EcoDeviceConfiguration ecoDeviceConfiguration

    def setupSpec() {
        ecoDeviceConfiguration = ConfigurationBuilder.loadConfiguration("eco-device", "gce-eco-device",
                EcoDeviceConfiguration.class);
    }

    /*
    def "test retrieval of teleinfo counters"() {
        given:
        def teleinfo = new Teleinfo(TELEINFO1)
        def teleinfoRetreiver = new EcoDeviceTeleinfoRetriever()
        def command = new FetchTeleinfoCountersCommand(teleinfo, ecoDeviceConfiguration, teleinfoRetreiver)

        when:
        Observable<Long> observable = Observable.merge(command.observe())
        def iterator = observable.toBlockingObservable().iterator
        def values = []
        values << iterator.next()
        values << iterator.next()

        then:
        values.size() == 2
        values[0] > 0
        values[1] > 0
    }
    */

    def "test retrieval of teleinfo counters"() {
        given:
        def teleinfo = new Teleinfo(TELEINFO1)
        def teleinfoRetriever = Mock(TeleinfoRetriever)
        teleinfoRetriever.retrieveIndexes(teleinfo, ecoDeviceConfiguration) >> Observable.create({ Observer observer ->
            observer.onNext(12L)
            observer.onNext(34L)
            observer.onCompleted()
        })
        def command = new FetchTeleinfoCountersCommand(teleinfo, ecoDeviceConfiguration, teleinfoRetriever)

        when:
        def observable = command.observe()

        then:
        observable != null

        when:
        def iterator = Observable.merge(observable).toBlockingObservable().iterator
        def values = []
        values << iterator.next()
        values << iterator.next()

        then:
//        1 * teleinfoRetriever.retrieveIndexes(teleinfo, ecoDeviceConfiguration)
        values.size() == 2
        values[0] > 0
        values[1] > 0
    }


}
