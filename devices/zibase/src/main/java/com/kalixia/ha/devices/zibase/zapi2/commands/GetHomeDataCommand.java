package com.kalixia.ha.devices.zibase.zapi2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.zibase.zapi2.ZibaseDeviceConfiguration;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.kalixia.ha.model.sensors.BasicSensor;
import com.kalixia.ha.model.sensors.Sensor;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.apache.http.ObservableHttp;
import rx.exceptions.Exceptions;

import javax.measure.unit.Unit;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Fetch token from login and password in order to be able to make ZAPI2 calls.
 *
 * https://zibase.net/api/get/ZAPI.php?login=[your login]&password=[your password]&service=get&target=token
 */
public class GetHomeDataCommand extends HystrixCommand<List<Sensor>> {
    private final String requestURL;
    private final CloseableHttpAsyncClient httpClient;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(GetHomeDataCommand.class);

    public GetHomeDataCommand(String token, ZibaseDeviceConfiguration configuration,
                              CloseableHttpAsyncClient httpClient, ObjectMapper mapper) {
        super(Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Zibase"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetHomeData"))
        );
        AuthenticationConfiguration authentication = configuration.getAuthentication();
        this.requestURL = String.format("%s?zibase=%s&token=%s&service=get&target=home",
                configuration.getUrl(), token, configuration.getZibaseID());
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @Override
    protected List<Sensor> run() throws Exception {
        logger.info("Collecting devices attached to the Zibase...");
        logger.debug("Request is: {}", requestURL);
        List<Sensor> sensors = ObservableHttp.createGet(requestURL, httpClient)
                .toObservable()
                .flatMap((response) -> response.getContent().map(String::new))
                .flatMap(this::extractSensorsFromJson)
                .toList().toBlockingObservable().single();
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
    private Observable<Sensor> extractSensorsFromJson(String json) {
        try {
            JsonNode rootNode = mapper.readTree(json);
            JsonNode sensorsNode = rootNode.get("body").get("sensors");
            return Observable.from(sensorsNode)
                    .map(node -> {
                        String sensorName = node.get("name").asText();
                        return new BasicSensor(sensorName, Unit.ONE);
                    });
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
