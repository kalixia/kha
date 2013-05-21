package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.model.Device;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Path("/devices")
@Produces("application/json")
public class DeviceResource {
    @Inject
    DevicesService service;

    @GET
    @Path("/")
    public List<? extends Device> findAllDevices() {
//        def devices = []
//        service.findAllDevices().subscribe({ Device device ->
//            println device
//            devices << device
//        })
        return Collections.emptyList();
    }

    @GET
    @Path("{id}")
    public Device findDeviceById(@PathParam("id") UUID id) {
        return null;
//        return service.findDeviceById(id).single()
    }
}
