package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.jaxrs.UriTemplateUtils;
import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.sensors.MutableSensor;
import com.kalixia.ha.model.sensors.Sensor;
import com.kalixia.ha.model.sensors.SensorBuilder;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import com.wordnik.swagger.annotations.Authorization;

import javax.inject.Inject;
import javax.measure.unit.Unit;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Path("/{username}/devices/{device}/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "sensors", description = "API for Sensors", position = 3)
public class SensorResource {
    @Inject
    DevicesService devicesService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a sensor",
            response = Sensor.class,
            authorizations = @Authorization(value = "api_key", type = "api_key"))
    @ApiResponses({
            @ApiResponse(code = 201, message = "if the sensor was created"),
            @ApiResponse(code = 417, message = "if the device for which the sensor should be created can't be found")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = RESTCodec.HEADER_REQUEST_ID, value = "ID of the request", required = false,
                    dataType = "uuid", paramType = "header")
    })
    public Response createSensor(@PathParam("username") String owner, @PathParam("device") String deviceName, Map json) throws URISyntaxException {
        String sensorName = (String) json.get("name");
        String unit = (String) json.get("unit");
        String type = (String) json.get("type");
        checkArgument(sensorName != null, "Missing device name");
        checkArgument(unit != null, "Missing device type");

        Device device = devicesService.findDeviceByName(owner, deviceName);
        if (device == null) {
            return Response
                    .status(Response.Status.EXPECTATION_FAILED)
                    .entity(Errors.withErrors(new ErrorMessage("Can't find device '%s' of '%s'", deviceName, owner)))
                    .build();
        }

        Sensor sensor = new SensorBuilder()
                .forDevice(device)
                .ofType(type)
                .withName(sensorName)
                .withUnit(Unit.valueOf(unit))
                .build();
        device.addSensor(sensor);
        devicesService.saveDevice(device);

        URI deviceURI = new URI(UriTemplateUtils.createURI("/{username}/devices/{device}", owner, device.getName()));
        return Response.created(deviceURI).entity(sensor).build();
    }
}
