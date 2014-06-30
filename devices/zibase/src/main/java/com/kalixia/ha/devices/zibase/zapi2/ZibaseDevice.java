package com.kalixia.ha.devices.zibase.zapi2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.devices.zibase.zapi2.commands.FetchTokenCommand;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.AbstractDevice;
import com.kalixia.ha.model.devices.DeviceBuilder;
import com.kalixia.ha.model.devices.PullBasedDevice;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class ZibaseDevice extends AbstractDevice<ZibaseDeviceConfiguration>
        implements PullBasedDevice<ZibaseDeviceConfiguration> {
    private ZibaseDeviceConfiguration configuration;
    private CloseableHttpAsyncClient httpClient;
    private ObjectMapper mapper;
    private String token;
    public static final String TYPE = "zibase";
    private static final Logger logger = LoggerFactory.getLogger(ZibaseDevice.class);

    public ZibaseDevice(DeviceBuilder builder) {
        super(builder);
    }

    @Override
    public void init(ZibaseDeviceConfiguration configuration) {
        this.configuration = configuration;
        try {
            // init HTTP client
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (chain, authType) -> true).build();
            httpClient = HttpAsyncClients.custom().setSSLContext(sslContext).build();
            httpClient.start();

            // configure Json parser
            mapper = new ObjectMapper();
            mapper.getFactory().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

            token = fetchToken();
            // TODO: fetch Zibase devices after init
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            logger.error("Can't initialize SSL engine", e);
        }
    }

    /**
     * Fetch ZAPI2 token
     * @return the ZAPI2 token
     */
    private String fetchToken() {
        return new FetchTokenCommand(configuration, httpClient, mapper).execute();
    }

    @Override
    public void fetchSensorsData() {
        // TODO:
    }

    @Override
    protected String getConfigurationFilename() {
        return "zibase-device";
    }

    @Override
    protected Class<ZibaseDeviceConfiguration> getConfigurationClass() {
        return ZibaseDeviceConfiguration.class;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
