package com.kalixia.ha.devices.weather.wunderground;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.weather.wunderground.sensors.WundergroundPressureSensor;
import com.kalixia.ha.devices.weather.wunderground.sensors.WundergroundTemperatureSensor;
import com.kalixia.ha.model.sensors.Sensor;
import com.kalixia.ha.model.sensors.SensorBuilder;
import com.kalixia.ha.model.sensors.SensorCreator;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;

import javax.measure.quantity.Quantity;

import static java.lang.String.format;

public class WundergroundSensorCreator implements SensorCreator<WundergroundDevice> {

    @Override
    public String getDeviceType() {
        return WundergroundDevice.TYPE;
    }

    @Override
    public Sensor<? extends Quantity> createFromBuilder(SensorBuilder<WundergroundDevice> builder) {
        WundergroundDevice device = builder.getDevice();
        WundergroundDeviceConfiguration configuration = device.getConfiguration();
        HttpClient<ByteBuf, ByteBuf> httpClient = device.getHttpClient();
        ObjectMapper mapper = device.getMapper();
        switch (builder.getType()) {
            case WundergroundTemperatureSensor.TYPE:
                return new WundergroundTemperatureSensor(configuration, httpClient, mapper);
            case WundergroundPressureSensor.TYPE:
                return new WundergroundPressureSensor(configuration, httpClient, mapper);
            default:
                throw new IllegalArgumentException(format("Can't process type %s", builder.getType()));
        }
    }

}
