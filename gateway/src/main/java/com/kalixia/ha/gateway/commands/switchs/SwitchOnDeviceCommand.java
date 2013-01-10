package com.kalixia.ha.gateway.commands.switchs;

import com.kalixia.ha.model.capabilities.Switch;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class SwitchOnDeviceCommand extends HystrixCommand<Switch.Status> {
    private final Switch device;

    public SwitchOnDeviceCommand(Switch device) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Switch"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("switch_off"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("Gateway")));
        this.device = device;
    }

    @Override
    protected Switch.Status run() throws Exception {
        device.on();
        return device.getStatus();
    }
}
