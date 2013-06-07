package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.model.Device;
import com.kalixia.ha.model.devices.RGBLamp;
import org.hibernate.validator.constraints.NotEmpty;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {
    @Inject
    DevicesService service;

    @GET
    public @NotEmpty List<? extends Device> findAllDevices() {
        return service.findAllDevices().toList().toBlockingObservable().single();
    }

    @GET
    @Path("{id}")
    public @NotNull Device findDeviceById(@PathParam("id") UUID id) {
        return service.findDeviceById(id).toBlockingObservable().single();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
//    public void createDevice(Map<String, String> jsonContent) {
    public void createDevice(Map json) {
        String name = (String) json.get("name");
        checkNotNull(name);
        service.createDevice(name, RGBLamp.class);
    }

}
