package com.kalixia.ha.devices.weather.wunderground.commands

import com.kalixia.ha.devices.weather.WeatherRequest
import junit.framework.Assert
import rx.functions.Action0
import rx.functions.Action1
import spock.lang.Unroll

import javax.measure.unit.SI

import static java.util.Locale.FRANCE
import static junit.framework.Assert.*

class ConditionsCommandTest extends AbstractWundergroundCommandTest {

    def "request current weather in various configurations"() {
        expect:
        conditions != ''
        conditions.temperature.doubleValue(SI.CELSIUS) > -10

        where:
        request << [
                new WeatherRequest().forCityInCountry("Paris", "France"),
                new WeatherRequest().forCityInCountry("Paris", "France").withLocale(FRANCE),
                new WeatherRequest().forCityInState("New York", "NY"),
                new WeatherRequest().forZipCode("10001")
        ]
        cmd = new ConditionsCommand(request, configuration, httpClient, mapper)
        conditions = cmd.observe().last().toBlocking().single()
    }

    @Unroll
    def "request current weather conditions with invalid location type #locationType"() {
        when:
        def obs = new ConditionsCommand(request, configuration, httpClient, mapper).observe()
        then:
        obs.subscribe({ fail() } as Action1, { } as Action1, { } as Action0)

//        when:
//        def observable = new ConditionsCommand(request, configuration, httpClient, mapper).observe()
//        then:
//        observable.subscribe(
//                { conditions -> fail("Unexpected result from observable") },
//                { error -> println "got expected error" }
//        )

        where:
        request << [
                new WeatherRequest().forLocalIP(),
                new WeatherRequest().forCoordinates(49.02000046f, 2.52999997f)
        ]
        locationType = request.locationType
    }

}