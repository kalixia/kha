package com.kalixia.ha.api.rest;

import com.kalixia.ha.model.Device;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Path("/fake")
@Produces("application/json")
public class FakeResource {

    @GET
    @Path("/")
    public List<? extends Device> findAllDevices() {
        return Collections.emptyList();
    }

    @GET
    @Path("{id}")
    public Device findDeviceById(@PathParam("id") UUID id) {
        return null;
    }
}
