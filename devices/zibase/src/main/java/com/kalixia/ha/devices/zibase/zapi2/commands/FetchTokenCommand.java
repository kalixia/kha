package com.kalixia.ha.devices.zibase.zapi2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.zibase.zapi2.ZibaseDeviceConfiguration;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.apache.http.ObservableHttp;
import rx.exceptions.Exceptions;

import java.io.IOException;

/**
 * Fetch token from login and password in order to be able to make ZAPI2 calls.
 *
 * https://zibase.net/api/get/ZAPI.php?login=[your login]&password=[your password]&service=get&target=token
 */
public class FetchTokenCommand extends HystrixCommand<String> {
    private final String requestURL;
    private final CloseableHttpAsyncClient httpClient;
    private final ObjectMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(FetchTokenCommand.class);

    public FetchTokenCommand(ZibaseDeviceConfiguration configuration,
                             CloseableHttpAsyncClient httpClient, ObjectMapper mapper) {
        super(HystrixCommand.Setter
                        .withGroupKey(HystrixCommandGroupKey.Factory.asKey("Zibase"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("FetchToken"))
        );
        AuthenticationConfiguration authentication = configuration.getAuthentication();
        this.requestURL = String.format("%s?login=%s&password=%s&service=get&target=token",
                configuration.getUrl(), authentication.getUsername(), authentication.getPassword());
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    @Override
    protected String run() throws Exception {
        logger.info("Fetching ZAPI2 token from credentials...");
        logger.debug("Request is: {}", requestURL);
        String token = ObservableHttp.createGet(requestURL, httpClient)
                .toObservable()
                .flatMap((response) -> response.getContent().map(String::new))
                .map(this::extractTokenFromJsonResponse)
                .toBlockingObservable().single();
        logger.info("Fetched token '{}'", token);
        return token;
    }

    /**
     * Parse Json response for the token.
     * @param json the Json response
     * @return the extract token
     *
     * Expect a Json response like this one:
     * <pre>
     * {
     *   “head”: “success”
     *   “body”: {
     *     “zibase” : "xxx",
     *     "token" : "xxx",
     *   }
     * }
     * </pre>
     */
    private String extractTokenFromJsonResponse(String json) {
        try {
            JsonNode rootNode = mapper.readTree(json);
            return rootNode.get("body").get("token").asText();
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
