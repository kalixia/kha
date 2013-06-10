package com.kalixia.ha.api.rest;

import com.kalixia.ha.api.DevicesFactory;
import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceID;
import com.kalixia.ha.model.devices.RGBLamp;
import com.kalixia.rawsag.codecs.jaxrs.UriTemplateUtils;
import rx.Observable;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Path("/{username}/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {
    @Inject
    DevicesService devicesService;

    @Inject
    UsersService usersService;

    @GET
    public
    @NotNull
    List<? extends Device> findAllDevicesOfUser(@PathParam("username") String username) {
        return devicesService.findAllDevicesOfUser(username).toList().toBlockingObservable().single();
    }

    @GET
    @Path("{name}")
    public
    @NotNull
    Device findDeviceById(@PathParam("username") String username, @PathParam("name") String name) {
        return devicesService.findDeviceById(new DeviceID(username, name)).toBlockingObservable().single();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createDevice(@PathParam("username") String username, Map json) throws URISyntaxException {
        String name = (String) json.get("name");
        String type = (String) json.get("type");
        checkArgument(name != null, "Missing device name");
        checkArgument(type != null, "Missing device type");
        if (!"RGBLamp".equals(type))
            throw new IllegalArgumentException(String.format("Invalid device type '%s'", type));

        User owner = usersService.findByUsername(username);
        if (owner == null) {
            return Response
                    .status(Response.Status.EXPECTATION_FAILED)
                    .entity(Errors.withErrors(new ErrorMessage("Can't find user '%s'", username)))
                    .build();
        }

        Device device = DevicesFactory.createDevice(name, owner, RGBLamp.class);
        if (devicesService.findDeviceById(device.getId()) == null) {
            devicesService.saveDevice(device);
            URI deviceURI = new URI(UriTemplateUtils.createURI(
                    "/{username}/devices/{device}",
                    username, device.getName()));
            return Response.created(deviceURI).build();
        } else {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The device '%s' for user '%s' already exists. " +
                                    "Use PUT method on the resource instead!",
                                    device.getName(), device.getOwner())))
                    .build();
        }
    }

    @PUT
    @Path("{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDevice(@PathParam("username") String username, @PathParam("name") String name, Map json)
            throws URISyntaxException {
        String newName = (String) json.get("name");
        String newType = (String) json.get("type");
        checkArgument(newName != null, "Missing device name");
        checkArgument(newType != null, "Missing device type");
        if (!"RGBLamp".equals(newType))
            throw new IllegalArgumentException(String.format("Invalid device type '%s'", newType));

        User owner = usersService.findByUsername(username);
        if (owner == null) {
            return Response
                    .status(Response.Status.EXPECTATION_FAILED)
                    .entity(Errors.withErrors(new ErrorMessage("Can't find user '%s'", username)))
                    .build();
        }

        Device device = DevicesFactory.createDevice(name, owner, RGBLamp.class);
        Observable<? extends Device> existingDevice = devicesService.findDeviceById(device.getId());
        if (existingDevice == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The device '%s' for user '%s' does not exist. " +
                                    "CreÍate it first via a POST request.", name, username)))
                    .build();
        } else {
            devicesService.saveDevice(device);
            return Response.ok().build();
        }
    }


}
