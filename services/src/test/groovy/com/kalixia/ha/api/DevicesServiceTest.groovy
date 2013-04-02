package com.kalixia.ha.api

import com.kalixia.ha.model.sensors.DataPoint
import org.joda.time.Instant

class DevicesServiceTest extends spock.lang.Specification {
    def DevicesService service = new DevicesServiceImpl()

    /*
    def "find all devices"() {
        given: "fetching all devices"
        def devices = service.findAllDevicesOfUser()
        def devicesAsList = devices.toList().last()
        expect: "get some"
//        devices.take(1)
//               .map({ it -> it.name })
//               .subscribe({ device ->
//            println device
//        })
        devicesAsList.size() > 0
        devicesAsList.size() == 2
    }
    */

    def "test data points"() {
        when:
        DataPoint<Double> dp = new DataPoint<>(12, new Instant())
        then:
        println "DataPoint is: $dp"
    }

//    def "test searching for missing device"() {
//        when: "searching for a device which does not exist"
//        def device = service.findDeviceById(UUID.randomUUID())
//        then:
//        device.next().name == '??'
//        device.subscribe(
//                { d -> println d },
//                { error -> println error }
//        )
//    }

    /*
    @Unroll
    def "uri template #uri_template is compiled to #pattern regex"() {
        expect:
        UriTemplateUtils.extractRegexPattern(uri_template).toString() == pattern

        where:
        uri_template                    | pattern
        "/devices"                      | "^/devices/?\$"
        "/devices/"                     | "^/devices/?\$"
        "/devices/{id}"                 | "^/devices/(.*)/?\$"
        "/devices/{id}/temperature"     | "^/devices/(.*)/temperature/?\$"
    }
    */

}