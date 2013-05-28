package com.kalixia.ha.gateway;

import com.kalixia.ha.api.ServicesModule;
import com.kalixia.ha.api.rest.GeneratedJaxRsDaggerModule;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
        injects = Main.class,
        includes = {
                GeneratedJaxRsDaggerModule.class,
                ServicesModule.class
        }
)
public class GatewayModule {

    @Provides @Singleton Gateway provideGateway(ApiServer apiServer) {
        return new GatewayImpl(apiServer);
    }

    @Provides @Singleton ApiServer provideApiServer(ApiServerChannelInitializer channelInitializer) {
        return new ApiServer(8082, channelInitializer);
    }

}
