package com.kalixia.ha.devices.zibase.zapi2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.zibase.zapi2.ZibaseDevice;
import com.kalixia.ha.model.quantity.WattsPerHour;
import com.kalixia.ha.model.sensors.DataPoint;
import com.kalixia.ha.model.sensors.Sensor;
import com.kalixia.ha.model.sensors.SensorBuilder;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.exceptions.Exceptions;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Quantity;
import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import java.io.IOException;
import java.util.List;

import static java.lang.String.format;

/**
 * Fetch token from login and password in order to be able to make ZAPI2 calls.
 *
 * https://zibase.net/api/get/ZAPI.php?login=[your login]&password=[your password]&service=get&target=token
 */
public class GetHomeDataCommand extends HystrixCommand<List<Sensor<?>>> {
    private final String requestURL;
    private final ZibaseDevice device;
    private final CloseableHttpAsyncClient httpClient;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(GetHomeDataCommand.class);

    public GetHomeDataCommand(String token, ZibaseDevice device,
                              CloseableHttpAsyncClient httpClient, ObjectMapper mapper) {
        super(Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Zibase"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetHomeData"))
        );
        this.requestURL = format("%s?zibase=%s&token=%s&service=get&target=home",
                device.getConfiguration().getUrl(), device.getConfiguration().getZibaseID(), token);
        this.device = device;
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @Override
    protected List<Sensor<?>> run() throws Exception {
        logger.info("Collecting devices attached to the Zibase...");
        logger.debug("Request is: {}", requestURL);
        List<Sensor<?>> sensors = ObservableHttp.createGet(requestURL, httpClient)
                .toObservable()
                .flatMap((response) -> response.getContent().map(String::new))
                .flatMap(this::extractSensorsFromJson)
                .toList().toBlockingObservable().single();
        logger.info("Found {} sensors", sensors.size());
        return sensors;
    }

    /**
     * Parse Json response from the request.
     * @param json the Json response
     * @return the extract token
     *
     * Expected a Json response like this one:
     * <pre>
     * {
     *   “head”: “success”
     *   “body”: {
     *     “zibase” : "xxx",
     *     "actuators" : [
     *       { “id” : “xxx”, “name” : “xxx”, “icon” : “xxx”, “protocol” : “xxx”, “status” : “xxx” },
     *       etc.
     *     ],
     *     "sensors" : [
     *       { “id” : “xxx”, “name” : “xxx”, “icon” : “xxx”, “protocol” : “xxx”, “status” : “xxx” },
     *       etc.
     *     ],
     *     etc.
     *   }
     * }
     * </pre>
     */
    private Observable<Sensor<? extends Quantity>> extractSensorsFromJson(String json) {
        try {
            JsonNode rootNode = mapper.readTree(json);
            JsonNode probesNode = rootNode.get("body").get("probes");
            return Observable.from(probesNode)
                    .flatMap(probe -> {
                        logger.debug("Found probe {}", probe);

                        String sensorName = probe.get("name").asText();
                        String sensorType = probe.get("type").asText();

                        // skip probe if not active
                        if (!probe.get("status").asBoolean()) {
                            logger.warn("Probe '{}' of type '{}' is not active, skipping it...", sensorName, sensorType);
                            return Observable.empty();
                        }

                        Instant instant;
                        if (probe.has("time"))
                            instant = new Instant(probe.get("time").asLong() * 1000);
                        else
                            instant = DateTime.now().toInstant();

                        switch (sensorType) {
                            case "power":
                                double wph = probe.get("val1").asDouble();
                                DataPoint<WattsPerHour> dataPoint = new DataPoint<>(
                                        Measure.valueOf(wph, WattsPerHour.UNIT), instant);
                                Sensor<WattsPerHour> powerSensor = new SensorBuilder<ZibaseDevice>()
                                        .forDevice(device)
                                        .ofType("power")
                                        .withName(sensorName)
                                        .withUnit(WattsPerHour.UNIT)
                                        .withLastValue(dataPoint)
                                        .build();
                                return Observable.just(powerSensor);
                            case "temperature":
                                double tempInCelsius = probe.get("val1").asDouble();
                                double humidityValue = probe.get("val2").asDouble();

                                DataPoint<Temperature> temperatureDataPoint = new DataPoint<>(
                                        Measure.valueOf(tempInCelsius, SI.CELSIUS), instant);
                                Sensor<Temperature> temperatureSensor = new SensorBuilder<ZibaseDevice>()
                                        .forDevice(device)
                                        .ofType("temperature")
                                        .withName(sensorName)
                                        .withUnit(SI.CELSIUS)
                                        .withLastValue(temperatureDataPoint)
                                        .build();

                                DataPoint<Dimensionless> humidityDataPoint = new DataPoint<>(
                                        Measure.valueOf(humidityValue, NonSI.PERCENT), instant);
                                Sensor<Temperature> humiditySensor = new SensorBuilder<ZibaseDevice>()
                                        .forDevice(device)
                                        .ofType("humidity")
                                        .withName(sensorName)
                                        .withUnit(NonSI.PERCENT)
                                        .withLastValue(humidityDataPoint)
                                        .build();

                                return Observable.from(temperatureSensor, humiditySensor);
                            default:
                                logger.warn("Don't know what to do of sensor with type '{}'", sensorType);
                                return Observable.error(new IllegalArgumentException(
                                        format("Don't know what to do of sensor with type '{}'", sensorType)));
                        }
                    });
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
