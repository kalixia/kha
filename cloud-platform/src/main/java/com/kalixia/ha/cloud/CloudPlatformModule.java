package com.kalixia.ha.cloud;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.kalixia.ha.api.ServicesModule;
import com.kalixia.ha.api.rest.GeneratedJaxRsDaggerModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Module(
        injects = Main.class,
        includes = {
                GeneratedJaxRsDaggerModule.class,
                ServicesModule.class
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

    @Provides @Singleton
    GraphiteReporter provideMetricRegistry(MetricRegistry registry) {
        final Graphite graphite = new Graphite(new InetSocketAddress("graphite.example.com", 2003));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                                                          .prefixedWith("web1.example.com")
                                                          .convertRatesTo(TimeUnit.SECONDS)
                                                          .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                          .filter(MetricFilter.ALL)
                                                          .build(graphite);
        return reporter;
    }

}
