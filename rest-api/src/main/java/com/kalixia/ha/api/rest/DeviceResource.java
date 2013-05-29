package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.model.Device;
import rx.Observable;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {
    @Inject
    DevicesService service;

    @GET
    public List<? extends Device> findAllDevices() {
        return service.findAllDevices().toList().toBlockingObservable().single();
    }

    @GET
    @Path("/new")
    public Observable<? extends Device> findAllDevicesThroughObservable() {
        return service.findAllDevices();
    }

    @GET
    @Path("{id}")
    public Observable<? extends Device> findDeviceById(@PathParam("id") UUID id) {
        return service.findDeviceById(id);
        //return service.findDeviceById(id).toBlockingObservable().single();
    }

}
