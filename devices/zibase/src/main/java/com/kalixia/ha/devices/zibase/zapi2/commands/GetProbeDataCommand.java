package com.kalixia.ha.devices.zibase.zapi2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kalixia.ha.devices.zibase.zapi2.ZibaseDeviceConfiguration;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.kalixia.ha.model.quantity.WattsPerHour;
import com.kalixia.ha.model.sensors.BasicSensor;
import com.kalixia.ha.model.sensors.DataPoint;
import com.kalixia.ha.model.sensors.Sensor;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Notification;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.exceptions.Exceptions;
import rx.functions.Action1;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Temperature;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class GetProbeDataCommand extends HystrixCommand<List<DataPoint>> {
    private final String probeID;
    private final String requestURL;
    private final CloseableHttpAsyncClient httpClient;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(GetProbeDataCommand.class);

    public GetProbeDataCommand(String probeID, String token, ZibaseDeviceConfiguration configuration,
                               CloseableHttpAsyncClient httpClient, ObjectMapper mapper, DateMidnight... date) {
        super(Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Zibase"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetProbeData"))
        );
        this.probeID = probeID;
        if (date.length == 1) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.FRENCH);
            String dateString = formatter.print(date[0]);
            this.requestURL = String.format("%s?zibase=%s&token=%s&service=get&target=probe&id=%s&historic=%s",
                    configuration.getUrl(), configuration.getZibaseID(), token, probeID, dateString);
        } else {
            this.requestURL = String.format("%s?zibase=%s&token=%s&service=get&target=probe&id=%s",
                    configuration.getUrl(), configuration.getZibaseID(), token, probeID);
        }
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @Override
    protected List<DataPoint> run() throws Exception {
        logger.info("Fetching sensor data for '{}'...", probeID);
        logger.debug("Request is: {}", requestURL);
        List<DataPoint> dataPoints = ObservableHttp.createGet(requestURL, httpClient)
                .toObservable()
                .flatMap((response) -> response.getContent().map(String::new))
                .flatMap(this::extractSensorsDataFromJson)
                .toList().toBlockingObservable().single();
        logger.info("Found {} datapoints", dataPoints.size());
        return dataPoints;
    }

    /**
     * Parse Json response from the request.
     * @param json the Json response
     * @return the extract token
     *
     * Expect a Json response like this one:
     * <pre>
     * {
     *   “head”: “success”,
     *   “body”: {
     *     “id” : “xxx”,
     *     “zibase” : "xxx",
     *     "data1Hours" : [
     *       [time, value],
     *       [time, value],
     *       etc
     *     ],
     *     "data2Hours" : [
     *       [time, value],
     *       [time, value],
     *       etc
     *     ]
     *   }
     * }
     * </pre>
     */
    private Observable<DataPoint> extractSensorsDataFromJson(String json) {
        logger.debug("Json is: {}", json);
        try {
            JsonNode rootNode = mapper.readTree(json);

            // TODO: find out why the Zibase API is broken!!!
            JsonNode probe = rootNode.get("body");
            Instant instant;

            String sensorType = probe.get("type").asText();

            if (probe.has("time"))
                instant = new Instant(probe.get("time").asLong() * 1000);
            else
                instant = DateTime.now().toInstant();

            switch (sensorType) {
                case "power":
                    double wph = probe.get("val1").asDouble();
                    DataPoint dataPoint = new DataPoint<>(Measure.valueOf(wph, WattsPerHour.UNIT), instant);
                    return Observable.just(dataPoint);
                case "temperature":
                    Double tempInCelsius = probe.get("val1").asDouble();
                    Double humidityValue = probe.get("val2").asDouble();
                    DataPoint dataPoint1 = new DataPoint<>(Measure.valueOf(tempInCelsius, SI.CELSIUS), instant);
                    DataPoint dataPoint2 = new DataPoint<>(Measure.valueOf(humidityValue, NonSI.PERCENT), instant);
                    return Observable.from(dataPoint1, dataPoint2);
                default:
                    return Observable.empty();
            }

            /*
            JsonNode data1 = rootNode.get("body").get("data1Hours");
            return Observable.from(data1)
                    .flatMap(data -> {
                        logger.info("Found data: {}", data);
                        /*
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

                        DataPoint dataPoint;
                        switch (sensorType) {
                            case "power":
                                double wph = probe.get("val1").asDouble();
                                dataPoint = new DataPoint<>(Measure.valueOf(wph, WattsPerHour.UNIT), instant);
                                return Observable.just(new BasicSensor<>(sensorName, WattsPerHour.UNIT, dataPoint));
                            case "temperature":
                                double tempInCelsius = probe.get("val1").asDouble();
                                double humidityValue = probe.get("val2").asDouble();
                                dataPoint = new DataPoint<>(Measure.valueOf(tempInCelsius, SI.CELSIUS), instant);
                                BasicSensor<Temperature> tempSensor = new BasicSensor<>(sensorName, SI.CELSIUS, dataPoint);
                                dataPoint = new DataPoint<>(Measure.valueOf(humidityValue, NonSI.PERCENT), instant);
                                BasicSensor<Dimensionless> humiditySensor = new BasicSensor<>(sensorName, NonSI.PERCENT, dataPoint);
                                return Observable.from(tempSensor, humiditySensor);
                            default:
                                logger.warn("Don't know what to do of sensor with type '{}'", sensorType);
                                return Observable.just(new BasicSensor(sensorName, Unit.ONE));
                        }
                    });
            */
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
