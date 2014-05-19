package com.kalixia.ha.hub;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.kalixia.ha.api.ServicesModule;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.api.rest.GeneratedJaxRsDaggerModule;
import com.kalixia.ha.api.rest.json.JScienceModule;
import com.kalixia.ha.dao.lucene.LuceneConfiguration;
import com.kalixia.ha.dao.lucene.LuceneModule;
import com.kalixia.ha.model.configuration.ConfigurationBuilder;
import dagger.Module;
import dagger.Provides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;

@Module(
        injects = Main.class,
        includes = {
                GeneratedJaxRsDaggerModule.class,
                ServicesModule.class,
                LuceneModule.class
        }
)
public class HubModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(HubModule.class);

    @Provides @Singleton Hub provideHub(ApiServer apiServer, WebAppServer webAppServer, JmxReporter reporter) {
        return new HubImpl(apiServer, webAppServer, reporter);
    }

    @Provides @Singleton HubConfiguration provideHubConfiguration() {
        try {
            return ConfigurationBuilder.loadConfiguration("hub", "hub", HubConfiguration.class);
        } catch (IOException e) {
            LOGGER.error("Can't load Hub configuration...", e);
            System.exit(-1);
            return null;
        }
    }

    @Provides @Singleton ApiServer provideApiServer(ApiServerChannelInitializer channelInitializer) {
        return new ApiServer(8082, channelInitializer);
    }

    @Provides @Singleton WebAppServer provideWebAppServer() {
        return new WebAppServer(8080);
    }

    @Provides @Singleton JmxReporter provideJmxReporter(MetricRegistry registry) {
        return JmxReporter.forRegistry(registry).inDomain("com.kalixia.ha.api.rest").build();
    }

    @Provides @Singleton ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // register Jackson modules
        mapper.registerModule(new JodaModule());
        mapper.registerModule(new JScienceModule());
//        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new AfterburnerModule());

        return mapper;
    }

    @Provides @Singleton LuceneConfiguration provideLuceneConfiguration(HubConfiguration config) {
        return config.getStorage().getLucene();
    }

}
