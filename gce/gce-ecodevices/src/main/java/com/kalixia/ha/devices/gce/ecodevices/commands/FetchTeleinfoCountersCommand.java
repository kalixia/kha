package com.kalixia.ha.devices.gce.ecodevices.commands;

import com.kalixia.ha.devices.gce.ecodevices.EcoDeviceConfiguration;
import com.kalixia.ha.devices.gce.ecodevices.Teleinfo;
import com.kalixia.ha.devices.gce.ecodevices.TeleinfoRetriever;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

public class FetchTeleinfoCountersCommand extends HystrixCommand<Observable<Long>> {
    private final Teleinfo teleinfo;
    private final EcoDeviceConfiguration configuration;
    private final TeleinfoRetriever retriever;
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTeleinfoCountersCommand.class);

    public FetchTeleinfoCountersCommand(Teleinfo teleinfo, EcoDeviceConfiguration configuration,
                                        TeleinfoRetriever retriever) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("EcoDevice"))
                .andCommandKey(HystrixCommandKey.Factory.asKey(teleinfo.getName().getName())));
        this.teleinfo = teleinfo;
        this.configuration = configuration;
        this.retriever = retriever;
    }

    @Override
    protected Observable<Long> run() throws Exception {
        return retriever.retrieveIndexes(teleinfo, configuration);
    }
}
