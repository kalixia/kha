package com.kalixia.ha.devices.weather.wunderground;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.weather.WeatherConditions;
import com.kalixia.ha.devices.weather.WeatherRequest;
import com.kalixia.ha.devices.weather.WeatherService;
import com.kalixia.ha.devices.weather.wunderground.commands.ConditionsCommand;
import com.kalixia.ha.devices.weather.wunderground.sensors.WundergroundPressureSensor;
import com.kalixia.ha.devices.weather.wunderground.sensors.WundergroundTemperatureSensor;
import com.kalixia.ha.model.devices.AbstractDevice;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.devices.PullBasedDevice;
import com.netflix.hystrix.HystrixCommand;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

public class WundergroundDevice extends AbstractDevice<WundergroundDeviceConfiguration>
        implements WeatherService, PullBasedDevice<WundergroundDeviceConfiguration> {
    @JsonProperty("configuration")
    private WundergroundDeviceConfiguration configuration;

    @JsonIgnore
    private HttpClient<ByteBuf, ByteBuf> httpClient;

    @JsonIgnore
    private ObjectMapper mapper;

    public static final String TYPE = "wunderground";

    @JsonIgnore
    private static final Logger LOGGER = LoggerFactory.getLogger(WundergroundDevice.class);

    public WundergroundDevice(DeviceBuilder builder) {
        super(builder);
    }

    @Override
    public void init(WundergroundDeviceConfiguration configuration) {
        this.configuration = configuration;
        // init HTTP client
        httpClient = RxNetty.createHttpClient(configuration.getHost(), configuration.getPort());

        // configure Json parser
        mapper = new ObjectMapper();
        mapper.getFactory().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        WundergroundTemperatureSensor temperatureSensor =
                new WundergroundTemperatureSensor(configuration, httpClient, mapper);
        WundergroundPressureSensor pressureSensor =
                new WundergroundPressureSensor(configuration, httpClient, mapper);

        addSensors(temperatureSensor, pressureSensor);
    }

    @Override
    public void fetchSensorsData() {
        // TODO:
    }

    @Override
    public Observable<WeatherConditions> getConditions(Observable<WeatherRequest> requests) {
        return requests
                .map(request -> new ConditionsCommand(request, configuration, httpClient, mapper))
                .flatMap(command -> command.observe());
    }

    @Override
    protected String getConfigurationFilename() {
        return "wunderground-device";
    }

    @Override
    protected Class<WundergroundDeviceConfiguration> getConfigurationClass() {
        return WundergroundDeviceConfiguration.class;
    }

    public HttpClient<ByteBuf, ByteBuf> getHttpClient() {
        return httpClient;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
