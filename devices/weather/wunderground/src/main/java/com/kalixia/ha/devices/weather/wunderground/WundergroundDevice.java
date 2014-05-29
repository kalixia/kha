package com.kalixia.ha.devices.weather.wunderground;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.weather.WeatherConditions;
import com.kalixia.ha.devices.weather.WeatherRequest;
import com.kalixia.ha.devices.weather.WeatherService;
import com.kalixia.ha.devices.weather.wunderground.commands.ConditionsCommand;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.AbstractDevice;
import com.kalixia.ha.model.devices.PullBasedDevice;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;

import java.util.UUID;

public class WundergroundDevice extends AbstractDevice<WundergroundDeviceConfiguration> implements WeatherService, PullBasedDevice {
    private WundergroundDeviceConfiguration configuration;
    private HttpClient<ByteBuf, ByteBuf> httpClient;
    private ObjectMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(WundergroundDevice.class);

    public WundergroundDevice(UUID id, String name, User owner) {
        super(id, name, owner);
    }

    @Override
    public void init(WundergroundDeviceConfiguration configuration) {
        this.configuration = configuration;
        // init HTTP client
        httpClient = RxNetty.createHttpClient(configuration.getHost(), configuration.getPort());

        // configure Json parser
        mapper = new ObjectMapper();
        mapper.getFactory().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    @Override
    public void fetchSensorsData() {
        // TODO:
    }

    @Override
    public Observable<WeatherConditions> getConditions(Observable<WeatherRequest> requests) {
        return requests.flatMap(request -> new ConditionsCommand(request, configuration, httpClient, mapper).observe());
    }

    @Override
    protected String getConfigurationFilename() {
        return "wunderground-device";
    }

    @Override
    protected Class<WundergroundDeviceConfiguration> getConfigurationClass() {
        return WundergroundDeviceConfiguration.class;
    }

}
