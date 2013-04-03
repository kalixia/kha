package com.kalixia.ha.api.rest

import com.kalixia.ha.api.SensorsService
import com.kalixia.ha.model.Device
import com.kalixia.ha.model.sensors.Sensor

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("/devices/{deviceID}/sensors")
@Produces("application/json")
class SensorsResource {
    @Inject
    private SensorsService service;

    @POST
    @Path("/")
    public void createSensor(Sensor sensor) {
        service.createSensor(sensor)
    }

    @GET
    @Path("/{sensorID}")
    public Double getLastValue(@PathParam("sensorID") UUID sensorID) {
        return service.getLastValue(sensorID)
    }

}
