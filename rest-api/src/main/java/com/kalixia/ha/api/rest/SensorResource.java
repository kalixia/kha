package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.SensorsService;
import com.kalixia.ha.model.sensors.DataPoint;
import com.kalixia.ha.model.sensors.Sensor;
import rx.Observable;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/devices/{deviceID}/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {
    @Inject
    SensorsService service;

//    @POST
//    @Path("/")
//    public void createSensor(Sensor sensor) {
//        service.createSensor(sensor);
//    }

    @GET
    @Path("/{sensorID}")
    public DataPoint getLastValue(@PathParam("sensorID") UUID sensorID) {
        return service.getLastValue(sensorID).toBlockingObservable().single();
    }
}
