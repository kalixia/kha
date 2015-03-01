package com.kalixia.ha.devices.weather.wunderground.sensors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.weather.WeatherConditions;
import com.kalixia.ha.devices.weather.WeatherRequest;
import com.kalixia.ha.devices.weather.wunderground.WundergroundDeviceConfiguration;
import com.kalixia.ha.devices.weather.wunderground.commands.ConditionsCommand;
import com.kalixia.ha.model.sensors.AbstractSensor;
import com.kalixia.ha.model.sensors.DataPoint;
import com.kalixia.ha.model.sensors.Sensor;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import org.joda.time.Instant;

import javax.measure.quantity.Pressure;
import javax.measure.unit.Unit;

import java.util.Objects;

import static com.google.common.base.Objects.toStringHelper;
import static javax.measure.unit.SI.PASCAL;

// TODO: rewrite this as an AggregratedSensor in order to avoid multiple requests!
public class WundergroundPressureSensor extends AbstractSensor<Pressure> {
    @JsonIgnore
    private final ConditionsCommand command;

    public static final String NAME = "pressure";
    public static final Unit<Pressure> UNIT = PASCAL.times(100);
    public static final String TYPE = "wunderground-pressure";

    public WundergroundPressureSensor(WundergroundDeviceConfiguration configuration,
                                      HttpClient<ByteBuf, ByteBuf> httpClient,
                                      ObjectMapper mapper) {
        WeatherRequest request = new WeatherRequest()
                        .forCityInCountry(configuration.getCity(), configuration.getCountry());
        this.command = new ConditionsCommand(request, configuration, httpClient, mapper);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Unit<Pressure> getUnit() {
        return UNIT;
    }

    @Override
    public DataPoint<Pressure> getLastValue() {
        WeatherConditions conditions = command.observe().last().toBlocking().single();
        return new DataPoint<>(conditions.getPressure(), Instant.now());
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
