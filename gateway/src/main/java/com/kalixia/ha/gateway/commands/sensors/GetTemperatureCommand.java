package com.kalixia.ha.gateway.commands.sensors;

import com.kalixia.ha.model.sensors.Temperature;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class GetTemperatureCommand extends HystrixCommand<Float> {
    private final Temperature device;

    public GetTemperatureCommand(Temperature device) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Temperature"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("get_temperature"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("Gateway")));
        this.device = device;
    }

    @Override
    protected Float run() throws Exception {
        return device.getCelsius();
    }
}
