package com.kalixia.ha.devices.gce.ecodevices.commands;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class FetchCounterValueCommand extends HystrixCommand<Long> {
    private final String name;

    public FetchCounterValueCommand(String name) {
        super(HystrixCommandGroupKey.Factory.asKey("EcoDevice"));
        this.name = name;
    }

    @Override
    protected Long run() throws Exception {
        return null;
    }
}
