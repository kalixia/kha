package com.kalixia.ha.gateway.resources;

import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.devices.RGBLamp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Path("/devices")
@Produces("application/json")
public class DeviceResource {

    @GET
    @Path("/")
    public List<? extends Device> findAllDevices() {
        // TODO: real implementation!
        return Arrays.asList(
                new RGBLamp(UUID.randomUUID(), "test"),
                new RGBLamp(UUID.randomUUID(), "another")
        );
    }

    @GET
    @Path("{id}")
    public Device findDeviceById(@PathParam("id") UUID id) {
        // TODO: real implementation!
        return new RGBLamp(id, "test");
    }
}
