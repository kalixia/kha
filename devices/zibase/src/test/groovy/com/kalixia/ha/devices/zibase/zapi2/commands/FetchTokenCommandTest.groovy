package com.kalixia.ha.devices.zibase.zapi2.commands

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.kalixia.ha.devices.zibase.zapi2.ZibaseDeviceConfiguration
import com.kalixia.ha.model.configuration.ConfigurationBuilder
import org.apache.http.conn.ssl.SSLContexts
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
import spock.lang.Shared
import spock.lang.Specification

import javax.net.ssl.SSLContext
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class FetchTokenCommandTest extends Specification {
    @Shared ZibaseDeviceConfiguration configuration
    @Shared CloseableHttpAsyncClient httpClient
    @Shared ObjectMapper mapper

    def setupSpec() {
        System.setProperty("HA_HOME", new File("src/main").getAbsolutePath())
        configuration = ConfigurationBuilder.loadConfiguration("my zibase", "zibase-device", ZibaseDeviceConfiguration.class)
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build()
        httpClient = HttpAsyncClients.custom().setSSLContext(sslContext).build()
        httpClient.start()
        mapper = new ObjectMapper()
        mapper.getFactory().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
    }

    def "fetch token from demo account"() {
        given:
        FetchTokenCommand cmd = new FetchTokenCommand(configuration, httpClient, mapper)

        when:
        def token = cmd.execute()

        then:
        token != ''
    }

}