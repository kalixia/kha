package com.kalixia.ha.devices.weather.wunderground.sensors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.weather.WeatherRequest;
import com.kalixia.ha.devices.weather.wunderground.WundergroundDeviceConfiguration;
import com.kalixia.ha.devices.weather.wunderground.commands.ConditionsCommand;
import com.kalixia.ha.model.sensors.TemperatureSensor;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;

import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;

// TODO: rewrite this as an AggregratedSensor in order to avoid multiple requests!
public class WundergroundTemperatureSensor extends TemperatureSensor {
    @JsonIgnore
    private final ConditionsCommand command;

    public static final String TYPE = "wunderground-temperature";

    public WundergroundTemperatureSensor(WundergroundDeviceConfiguration configuration,
                                         HttpClient<ByteBuf, ByteBuf> httpClient,
                                         ObjectMapper mapper) {
        WeatherRequest request = new WeatherRequest()
                .forCityInCountry(configuration.getCity(), configuration.getCountry());
        this.command = new ConditionsCommand(request, configuration, httpClient, mapper);
    }

    @Override
    protected Float getCelsius() {
        return (float) command.observe().last().toBlocking().single().getTemperature().doubleValue(getUnit());
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
