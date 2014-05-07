package com.kalixia.ha.devices.zibase.zapi2.commands;

import com.kalixia.ha.devices.zibase.zapi2.ZibaseDeviceConfiguration;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.apache.http.ObservableHttp;

/**
 * Fetch token from login and password in order to be able to make ZAPI2 calls.
 *
 * https://zibase.net/api/get/ZAPI.php?login=[your login]&password=[your password]&service=get&target=token
 */
public class GetHomeDataCommand extends HystrixCommand<String> {
    private final String requestURL;
    private final CloseableHttpAsyncClient httpClient;
    private static final Logger logger = LoggerFactory.getLogger(GetHomeDataCommand.class);

    public GetHomeDataCommand(String token, ZibaseDeviceConfiguration configuration, CloseableHttpAsyncClient httpClient) {
        super(Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Zibase"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetHomeData"))
        );
        AuthenticationConfiguration authentication = configuration.getAuthentication();
        this.requestURL = String.format("%s?zibase=%s&token=%s&service=get&target=home",
                configuration.getUrl(), token, configuration.getZibaseID());
        this.httpClient = httpClient;
    }

    @Override
    protected String run() throws Exception {
        logger.info("Collecting devices attached to the Zibase...");
        logger.debug("Request is: {}", requestURL);
        String token = ObservableHttp.createGet(requestURL, httpClient)
                .toObservable()
                .flatMap((response) -> response.getContent().map(String::new))
                .map(this::extractTokenFromJsonResponse)
                .toBlockingObservable().single();
        return token;
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
    private String extractTokenFromJsonResponse(String json) {
        return json;
    }

}
