package com.kalixia.ha.devices.weather.wunderground.commands

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import com.kalixia.ha.devices.weather.wunderground.WundergroundDeviceConfiguration
import com.kalixia.ha.model.configuration.ConfigurationBuilder
import io.netty.buffer.ByteBuf
import io.reactivex.netty.RxNetty
import io.reactivex.netty.protocol.http.client.HttpClient
import spock.lang.Shared
import spock.lang.Specification

abstract class AbstractWundergroundCommandTest extends Specification {
    @Shared WundergroundDeviceConfiguration configuration
    @Shared HttpClient<ByteBuf, ByteBuf> httpClient
    @Shared ObjectMapper mapper

    def setupSpec() {
        System.setProperty("app.home", new File("src/main").getAbsolutePath())
        configuration = ConfigurationBuilder.loadConfiguration("my wunderground", "wunderground-device", WundergroundDeviceConfiguration.class)
        httpClient = RxNetty.createHttpClient(configuration.getHost(), configuration.getPort())
        mapper = new ObjectMapper()
        mapper.getFactory().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
    }

}