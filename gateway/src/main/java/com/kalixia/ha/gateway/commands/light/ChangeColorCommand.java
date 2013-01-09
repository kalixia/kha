package com.kalixia.ha.gateway.commands.light;

import com.kalixia.ha.model.Color;
import com.kalixia.ha.model.capabilities.Light;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class ChangeColorCommand extends HystrixCommand<Color> {
    private final Light device;
    private final Color color;

    public ChangeColorCommand(Light device, Color color) {
        super(HystrixCommandGroupKey.Factory.asKey("light"));
        this.device = device;
        this.color = color;
    }

    @Override
    protected Color run() throws Exception {
        device.setColor(color);
        return device.getColor();
    }
}
