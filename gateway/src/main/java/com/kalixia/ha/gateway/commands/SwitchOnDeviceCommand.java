package com.kalixia.ha.gateway.commands;

import com.kalixia.ha.model.capabilities.Switch;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class SwitchOnDeviceCommand extends HystrixCommand<Switch.Status> {
    private final Switch device;

    public SwitchOnDeviceCommand(Switch device) {
        super(HystrixCommandGroupKey.Factory.asKey("switch"));
        this.device = device;
    }

    @Override
    protected Switch.Status run() throws Exception {
        device.on();
        return device.getStatus();
    }
}
