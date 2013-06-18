package com.kalixia.ha.api.rest;

import com.kalixia.grapi.codecs.jaxrs.UriTemplateUtils;
import com.kalixia.ha.api.DevicesFactory;
import com.kalixia.ha.api.DevicesService;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.devices.Device;
import com.kalixia.ha.model.devices.DeviceID;
import com.kalixia.ha.model.devices.RGBLamp;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.CACHE_CONTROL;
import static javax.ws.rs.core.HttpHeaders.ETAG;
import static javax.ws.rs.core.HttpHeaders.LAST_MODIFIED;

@Path("/{username}/devices")
@Produces(MediaType.APPLICATION_JSON)
public class DeviceResource {
    @Inject
    DevicesService devicesService;

    @Inject
    UsersService usersService;

    @GET
    public @NotNull Response findAllDevicesOfUser(@PathParam("username") String username) {
        List<? extends Device> devices = devicesService.findAllDevicesOfUser(username)
                .toList().toBlockingObservable().single();
        return Response
                .ok(devices)
                .link(UriTemplateUtils.createURI("/{username}", username), "owner")
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
    }

    @GET
    @Path("{name}")
    public @NotNull Response findDeviceById(@PathParam("username") String username, @PathParam("name") String name) {
        Device device = devicesService.findDeviceById(new DeviceID(username, name));
        if (device == null)
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        return Response
                .ok(device)
                .link(UriTemplateUtils.createURI("/{username}", username), "owner")
                .header(LAST_MODIFIED, device.getLastUpdateDate())
                .header(ETAG, device.getLastUpdateDate())
                .header(CACHE_CONTROL, "max-age=60, must-revalidate")
                .build();
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

        Device device = DevicesFactory.createDevice(newName, owner, RGBLamp.class);
        Device existingDevice = devicesService.findDeviceById(new DeviceID(username, name));
        if (existingDevice == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(Errors.withErrors(
                            new ErrorMessage("The device '%s' for user '%s' does not exist. " +
                                    "Create it first via a POST request.", name, username)))
                    .build();
        } else {
            // ensure that this is the same device otherwise delete the old one and create the new one
            if (!existingDevice.getId().equals(device.getId())) {
                devicesService.deleteDevice(existingDevice.getId());
            }
            devicesService.saveDevice(device);
            return Response.ok().build();
        }
    }

}
