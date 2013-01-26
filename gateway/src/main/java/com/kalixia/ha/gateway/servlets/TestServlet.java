package com.kalixia.ha.gateway.servlets;

import com.kalixia.ha.gateway.commands.sensors.GetTemperatureCommand;
import com.kalixia.ha.gateway.commands.switchs.SwitchOffDeviceCommand;
import com.kalixia.ha.gateway.commands.switchs.SwitchOnDeviceCommand;
import com.kalixia.ha.gateway.commands.light.ChangeColorCommand;
import com.kalixia.ha.model.Color;
import com.kalixia.ha.model.devices.RGBLamp;
import com.netflix.hystrix.contrib.yammermetricspublisher.HystrixYammerMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

// TODO: delete this code -- only here for testing purposes!
public class TestServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        HystrixPlugins.getInstance().registerMetricsPublisher(new HystrixYammerMetricsPublisher());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RGBLamp lamp = new RGBLamp(UUID.randomUUID(), "test lamp");

        new SwitchOnDeviceCommand(lamp).queue();
        new SwitchOffDeviceCommand(lamp).queue();
        new SwitchOnDeviceCommand(lamp).queue();

        new ChangeColorCommand(lamp, new Color(120, 1, 1)).queue();

        new GetTemperatureCommand(lamp).queue();

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
