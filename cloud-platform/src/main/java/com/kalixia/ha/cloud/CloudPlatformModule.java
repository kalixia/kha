package com.kalixia.ha.cloud;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.kalixia.ha.api.InstallationService;
import com.kalixia.ha.api.ServicesModule;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.api.rest.GeneratedJaxRsDaggerModule;
import com.kalixia.ha.api.security.SecurityModule;
import com.kalixia.ha.dao.cassandra.CassandraModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Module(
        injects = Main.class,
        includes = {
                GeneratedJaxRsDaggerModule.class,
                ServicesModule.class,
                SecurityModule.class,
                CassandraModule.class
        }
)
public class CloudPlatformModule {

    @Provides @Singleton CloudPlatform provideCloudPlatform(ApiServer apiServer, WebAppServer webAppServer,
                                                            GraphiteReporter reporter) {
        return new CloudPlatformImpl(apiServer, webAppServer, reporter);
    }

    @Provides @Singleton ApiServer provideApiServer(ApiServerChannelInitializer channelInitializer) {
        return new ApiServer(8082, channelInitializer);
    }

    @Provides @Singleton WebAppServer provideWebAppServer() {
        return new WebAppServer(8080);
    }

    @Provides @Singleton InstallationService provideInstallationService() {
        return new CloudPlatformInstallationService();
    }

    @Provides @Singleton
    GraphiteReporter provideGraphiteReporter(MetricRegistry registry) {
        final Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                                                          .prefixedWith("cloud.kalixia.com")
                                                          .convertRatesTo(TimeUnit.SECONDS)
                                                          .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                          .filter(MetricFilter.ALL)
                                                          .build(graphite);
        return reporter;
    }

    @Provides @Singleton ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // register Jackson modules
        mapper.registerModule(new JodaModule());
//        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new AfterburnerModule());

        return mapper;
    }

}
